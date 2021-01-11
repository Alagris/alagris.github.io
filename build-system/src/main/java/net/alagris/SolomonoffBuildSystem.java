package net.alagris;


import com.moandjiezana.toml.Toml;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.File;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.*;

public class SolomonoffBuildSystem {

    public static class BuildConfig {
        Mealy[] mealy;
        Kolmogorov[] kolmogorov;
        Infer[] infer;
    }

    public static class Infer {
        String path;
        String algorithm; //"RPNI", "OSTIA"
        String name;//name of produced transducer
    }

    public static class Mealy {
        String path;
    }

    public static class Kolmogorov {
        String path;
    }

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
    runCompiler(File buildFile, ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Minimize<G> minimize)
            throws ExecutionException, InterruptedException, CompilationError {
        final Toml toml = new Toml().read(buildFile);
        final BuildConfig config = toml.to(BuildConfig.class);

        final ExecutorService pool = Executors.newWorkStealingPool();
        final Queue<Future<Void>> queue = new LinkedList<>();

        final SolomonoffWeightedParser.ConcurrentCollector collector =
                new SolomonoffWeightedParser.ConcurrentCollector();

        for (final Mealy mealy : config.mealy) {
            // define tasks
            queue.add(pool.submit(() -> {
                final File mealyFile = new File(mealy.path);
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

                final SolomonoffWeightedParser listener = new SolomonoffWeightedParser(collector);
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
        //Topological order will allow for most efficient parallel compilation, because the dependencies
        //of every variable will be submitted for compilation at earlier stages.
        final TopologicalOrderIterator<String, Object> dependencyOrder = new TopologicalOrderIterator<>(dependencyOf);

        final ConcurrentHashMap<String, Future<G>> compiled = new ConcurrentHashMap<>();
        //compile all of them in parallel
        while (dependencyOrder.hasNext()) {
            final String id = dependencyOrder.next();
            final VarDef var = definitions.get(id);
            if (var != null) {
                assert var.def != null : id;
                compiled.put(id, pool.submit(() -> minimize.minimize(var.def.compile(specs, i -> {
                            try {
                                final Future<G> f = compiled.get(i);
                                if (f == null) {
                                    final Var v = specs.borrowVariable(i);
                                    assert v != null;
                                    return specs.getGraph(v);
                                } else {
                                    return specs.specification().deepClone(f.get());
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

}
