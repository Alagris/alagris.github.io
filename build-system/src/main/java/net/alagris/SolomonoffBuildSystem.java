package net.alagris;

import net.alagris.TomlParser.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.checkerframework.checker.units.qual.A;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.*;

public class SolomonoffBuildSystem {

    interface Minimize<G> {
        G minimize(G g) throws CompilationError;
    }

    static class VarDef {
        final SolomonoffWeighted def;
        ArrayList<Pair<SolomonoffWeighted, SolomonoffWeighted>> funcTypes = new ArrayList<>();
        ArrayList<Pair<SolomonoffWeighted, SolomonoffWeighted>> productTypes = new ArrayList<>();

        VarDef(SolomonoffWeighted def) {
            this.def = def;
        }
    }
    
    public static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> void
    runCompiler(Config config, ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Minimize<G> minimize)
            throws ExecutionException, InterruptedException, CompilationError, IOException {

        final ExecutorService pool = Executors.newWorkStealingPool();
        final Queue<Future<Void>> queue = new LinkedList<>();

        final SolomonoffWeightedParser.ConcurrentCollector collector =
                new SolomonoffWeightedParser.ConcurrentCollector();

        // parse user's mealy files
        for (final Source sourceFile : config.source) {
            if (!sourceFile.path.endsWith(".mealy")) {
                continue;
            }
            queue.add(pool.submit(() -> {
                final File mealyFile = new File(sourceFile.path);
                if (!mealyFile.exists()) {
                    throw new CLIException.MealyFileException(mealyFile.getPath());
                }
                final SolomonoffGrammarLexer lexer =
                        new SolomonoffGrammarLexer(CharStreams.fromPath(mealyFile.toPath()));
                final SolomonoffGrammarParser parser =
                        new SolomonoffGrammarParser(new CommonTokenStream(lexer));

                parser.addErrorListener(new BaseErrorListener() {
                    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                            int charPositionInLine, String msg, RecognitionException e) {
                        System.err.println("line " + line + ":" + charPositionInLine + " " + msg + " " + e);
                    }
                });

                final SolomonoffWeightedParser listener = new SolomonoffWeightedParser(collector, sourceFile.path);
                ParseTreeWalker.DEFAULT.walk(listener, parser.start());
                assert listener.stack.isEmpty();
                return null;
            }));
        }

        //wait for termination of all tasks
        for (Future<Void> task : queue) {
            task.get();
        }
        queue.clear();

        // initialize dependency graph
        final DirectedAcyclicGraph<String, Object> dependencyOf =
                new DirectedAcyclicGraph<>(null, null, false);
        final Iterator<Var> builtInVariables = specs.iterateVariables();
        while (builtInVariables.hasNext()) {
            dependencyOf.addVertex(specs.getName(builtInVariables.next()));
        }

        // collect all definitions into a single hashmap together with their respective type judgements
        final HashMap<String, VarDef> definitions = new HashMap<>();
        for (Map.Entry<String, SolomonoffWeighted> def : collector.definitions.entrySet()) {
            final String id = def.getKey();
            dependencyOf.addVertex(id);
            definitions.put(id, new VarDef(def.getValue()));
        }

        //Build reversed dependency graph. A directed edge (X,Y) will represent that
        //X is a dependency of Y. This will be necessary to later build topological order in which
        // X comes before Y whenever (X,Y) is an edge.
        for (Pair<String, String> dependency : collector.dependsOn) {
            //!!!! Invert the order !!!!
            dependencyOf.addEdge(dependency.r(), dependency.l(), new Object());
        }


        //collect types
        for (SolomonoffWeightedParser.ConcurrentCollector.Type type : collector.types) {
            if (type.isFunction) definitions.get(type.var).funcTypes.add(Pair.of(type.in, type.out));
            else definitions.get(type.var).productTypes.add(Pair.of(type.in, type.out));
        }
        //Topological order will allow for most efficient parallel compila    public static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>>tion, because the dependencies
        //of every variable will be submitted for compilation at earlier stages.
        final TopologicalOrderIterator<String, Object> dependencyOrder = new TopologicalOrderIterator<>(dependencyOf);

        //
        final ConcurrentHashMap<String, String> validCache = testCache(config, collector.sourceFiles);

        final ConcurrentHashMap<String, Future<G>> compiled = new ConcurrentHashMap<>();
        final ConcurrentLinkedQueue<Pair<String, G>> toCache = new ConcurrentLinkedQueue<>();
        //compile all of them in parallel
        while (dependencyOrder.hasNext()) {
            final String id = dependencyOrder.next();
            final VarDef var = definitions.get(id);
            if (var != null) {
                assert var.def != null : id;
                compiled.put(id, pool.submit(() -> minimize.minimize(var.def.compile(specs, i -> {
                            Optional<G> cachedGraph =  getCached(config, id, validCache);
                            if (cachedGraph.isPresent()) {
                                return cachedGraph.get();
                            }

                            try {
                                final Future<G> f = compiled.get(i);
                                if (f == null) {
                                    final Var v = specs.borrowVariable(i);
                                    assert v != null;
                                    final G graph = specs.getGraph(v);
                                    if (config.cashing) {
                                        toCache.add(Pair.of(id, graph));
                                    }
                                    return graph;
                                } else {
                                    final G graph = specs.specification().deepClone(f.get());
                                    if (config.cashing) {
                                        toCache.add(Pair.of(id, graph));
                                    }
                                    return graph;
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ))));
            } else {
                assert specs.borrowVariable(id) != null : id;
            }
        }
        //wait for all
        for (Map.Entry<String, Future<G>> v : compiled.entrySet()) {
            final G g = v.getValue().get();
            final String id = v.getKey();
            assert specs.borrowVariable(id) == null : id;
            specs.introduceVariable(id, Pos.NONE, g, false);
        }
        
        if (config.cashing) {
            File directory = new File(config.cacheLocation);
            if (! directory.exists()){
                directory.mkdirs();
            }

            for (Pair<String, G> g : toCache) {
                pool.submit(() -> {
                    cacheGraph(config, g.l(), g.r());
                });
            }
        }
    }

    private static void importFromPackages() {
    }

    private static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>>
    Optional<G> getCached(Config config, String id, ConcurrentHashMap<String, String> cacheMapping) {
        String hashedName = cacheMapping.get(id);
        if (hashedName == null) {
            return Optional.empty();
        }
        G graph = null;
        try {
            FileInputStream fileIn = new FileInputStream(config.cacheLocation + hashedName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            graph = (G) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
        return Optional.of(graph);
    }

    private static ConcurrentHashMap<String, String> testCache(Config config,
                                                             ConcurrentHashMap<String, String> sourceFiles)
            throws IOException {
        ConcurrentHashMap<String, String> validCache = new ConcurrentHashMap<>();

        File cache = new File(config.cacheLocation);
        if (!cache.exists()) {
            return validCache;
        }
        FileFilter onlyFiles = new FileFilter() {
            public boolean accept(File file) {
                boolean isFile = file.isFile();
                if (isFile) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File[] files = cache.listFiles(onlyFiles);

        HashMap<String, FileTime> cacheModifications = new HashMap<>();
        for (final File f : files) {
            final FileTime modificationDate = (FileTime) Files.getAttribute(f.toPath(), "lastModifiedTime");
            cacheModifications.put(f.getName(), modificationDate);
        }

        sourceFiles.forEach( (id, file) -> {
            FileTime modificationDate;
            try {
                modificationDate = (FileTime) Files.getAttribute(Paths.get(file), "lastModifiedTime");
            } catch (IOException e) {
                modificationDate = null;
            }
            final String hashedName = Integer.toHexString(id.hashCode());
            final FileTime cacheDate = cacheModifications.get(hashedName);
//            if (cacheDate != null && cacheDate.after(modificationDate)) {
            if (cacheDate != null && cacheDate.compareTo(modificationDate) > 0) {
                validCache.put(id, hashedName);
            }
        });

        return validCache;
    }

    private static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>>
    void cacheGraph(Config config, String id, G graph) {

        String name = Integer.toHexString(id.hashCode());
        try {
            FileOutputStream fileOut = new FileOutputStream(config.cacheLocation + name);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(graph);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static OptimisedLexTransducer.OptimisedHashLexTransducer
    getCompiler() throws CompilationError, ExecutionException, InterruptedException {
        final OptimisedLexTransducer.OptimisedHashLexTransducer compiler =
                new OptimisedLexTransducer.OptimisedHashLexTransducer(
                        0,
                        Integer.MAX_VALUE,
                        false);
        return compiler;
    }

    public static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> void
    saveBinary(Config config, OptimisedLexTransducer.OptimisedHashLexTransducer compiler) {
        for (Target t : config.target) {
            LexUnicodeSpecification.Var<N, G> g = compiler.getTransducer(t.id);
            try (FileOutputStream f = new FileOutputStream(t.out)) {
                compiler.specs.compressBinary(g.graph, new DataOutputStream(new BufferedOutputStream(f)));
                return null;
            } catch (IOException e) {
                return e.toString();
            }
        }
    }

    public static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> void
    loadBinary(Config config, OptimisedLexTransducer.OptimisedHashLexTransducer compiler) {
        }
    }
}
