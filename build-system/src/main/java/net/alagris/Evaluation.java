package net.alagris;

import net.alagris.CompilationError.WeightConflictingToThirdState;
import net.alagris.Specification.*;
import net.alagris.LexUnicodeSpecification.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.NoViableAltException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ReplInfrastruct {
    private final static Random RAND = new Random();

    public interface ReplCommand<Result> {
        Result run(OptimisedLexTransducer.OptimisedHashLexTransducer compiler, Consumer<String> log, Consumer<String> debug, String args);
    }

    public static final ReplCommand<String> REPL_LOAD = (compiler, log, debug, args) -> {
        try {
            final long parsingBegin = System.currentTimeMillis();
            compiler.parse(CharStreams.fromFileName(args));
            debug.accept("Parsing took " + (System.currentTimeMillis() - parsingBegin) + " miliseconds");
            final long optimisingBegin = System.currentTimeMillis();
            debug.accept("Optimising took " + (System.currentTimeMillis() - optimisingBegin) + " miliseconds");
            final long ambiguityCheckingBegin = System.currentTimeMillis();
            compiler.checkStrongFunctionality();
            debug.accept("Checking ambiguity " + (System.currentTimeMillis() - ambiguityCheckingBegin) + " miliseconds");
            final long typecheckingBegin = System.currentTimeMillis();
            debug.accept("Typechecking took " + (System.currentTimeMillis() - typecheckingBegin) + " miliseconds");
            debug.accept("Total time " + (System.currentTimeMillis() - parsingBegin) + " miliseconds");
            return null;
        } catch (CompilationError | IOException e) {
            return e.toString();
        }
    };

    public static final ReplCommand<String> REPL_LIST = (compiler, logs, debug, args) -> compiler.specs.variableAssignments
            .keySet().toString();
    public static final ReplCommand<String> REPL_SIZE = (compiler, logs, debug, args) -> {
        try {
            RangedGraph<Pos, Integer, E, P> r = compiler.getOptimisedTransducer(args);
            return r == null ? "No such function!" : String.valueOf(r.size());
        } catch (CompilationError e) {
            return e.toString();
        }
    };
    public static final ReplCommand<String> REPL_EVAL = (compiler, logs, debug, args) -> {
        try {
            final String[] parts = args.split("\\s+", 2);
            if (parts.length != 2)
                return "Two arguments required 'transducerName' and 'transducerInput' but got "
                        + Arrays.toString(parts);
            final String transducerName = parts[0].trim();
            final String transducerInput = parts[1].trim();
            final long evaluationBegin = System.currentTimeMillis();
            final RangedGraph<Pos, Integer, E, P> graph = compiler.getOptimisedTransducer(transducerName);
            if (graph == null)
                return "Transducer '" + transducerName + "' not found!";
            final IntSeq input = ParserListener.parseCodepointOrStringLiteral(transducerInput);
            final IntSeq output = compiler.specs.evaluate(graph, input);
            final long evaluationTook = System.currentTimeMillis() - evaluationBegin;
            debug.accept("Took " + evaluationTook + " miliseconds");
            return output == null ? "No match!" : output.toStringLiteral();
        } catch (WeightConflictingToThirdState e) {
            return e.toString();
        }
    };
    public static final ReplCommand<String> REPL_RUN = (compiler, logs, debug, args) -> {
        final String[] parts = args.split("\\s+", 2);
        if (parts.length != 2)
            return "Two arguments required 'transducerName' and 'transducerInput' but got " + Arrays.toString(parts);
        final String pipelineName = parts[0].trim();
        if(!pipelineName.startsWith("@")) {
            return "Pipeline names must start with @";
        }
        final String pipelineInput = parts[1].trim();
        final long evaluationBegin = System.currentTimeMillis();
        final LexPipeline<net.alagris.HashMapIntermediateGraph.N<Pos, E>, HashMapIntermediateGraph<Pos, E, P>> pipeline = compiler
                .getPipeline(pipelineName.substring(1));
        if (pipeline == null)
            return "Pipeline '" + pipelineName + "' not found!";
        final IntSeq input = ParserListener.parseCodepointOrStringLiteral(pipelineInput);
        final IntSeq output = pipeline.evaluate(input);
        final long evaluationTook = System.currentTimeMillis() - evaluationBegin;
        debug.accept("Took " + evaluationTook + " miliseconds");
        return output == null ? "No match!" : output.toStringLiteral();
    };
    public static final ReplCommand<String> REPL_EXPORT = (compiler, logs, debug, args) -> {
        Var<net.alagris.HashMapIntermediateGraph.N<Pos, E>, HashMapIntermediateGraph<Pos, E, P>> g = compiler
                .getTransducer(args);
        try (FileOutputStream f = new FileOutputStream(args + ".star")) {
            compiler.specs.compressBinary(g.graph, new DataOutputStream(new BufferedOutputStream(f)));
            return null;
        } catch (IOException e) {
            return e.toString();
        }
    };

    public static final ReplCommand<String> REPL_IS_DETERMINISTIC = (compiler, logs, debug, args) -> {
        try {
            RangedGraph<Pos, Integer, E, P> r = compiler.getOptimisedTransducer(args);
            if (r == null)
                return "No such function!";
            return r.isDeterministic() == null ? "true" : "false";
        } catch (CompilationError e) {
            return e.toString();
        }
    };
    public static final ReplCommand<String> REPL_LIST_PIPES = (compiler, logs, debug, args) -> {
        return Specification.fold(compiler.specs.pipelines.keySet(), new StringBuilder(),
                (pipe, sb) -> sb.append("@").append(pipe).append(", ")).toString();
    };
    public static final ReplCommand<String> REPL_EQUAL = (compiler, logs, debug, args) -> {
        try {
            final String[] parts = args.split("\\s+", 2);
            if (parts.length != 2)
                return "Two arguments required 'transducerName' and 'transducerInput' but got "
                        + Arrays.toString(parts);
            final String transducer1 = parts[0].trim();
            final String transducer2 = parts[1].trim();
            RangedGraph<Pos, Integer, E, P> r1 = compiler.getOptimisedTransducer(transducer1);
            RangedGraph<Pos, Integer, E, P> r2 = compiler.getOptimisedTransducer(transducer2);
            if (r1 == null)
                return "No such transducer '" + transducer1 + "'!";
            if (r2 == null)
                return "No such transducer '" + transducer2 + "'!";
            final AdvAndDelState<Integer, IntQueue> counterexample = compiler.specs.areEquivalent(r1, r2);
            if (counterexample == null)
                return "true";
            return "false";
        } catch (CompilationError e) {
            return e.toString();
        }
    };
    public static final ReplCommand<String> REPL_RAND_SAMPLE = (compiler, logs, debug, args) -> {
        try {
            final String[] parts = args.split("\\s+", 4);
            if (parts.length != 3) {
                return "Three arguments required: 'transducerName', 'mode' and 'size'";
            }
            final String transducerName = parts[0].trim();
            final String mode = parts[1];
            final int param = Integer.parseInt(parts[2].trim());
            final RangedGraph<Pos, Integer, E, P> transducer = compiler.getOptimisedTransducer(transducerName);
            if (mode.equals("of_size")) {
                final int sampleSize = param;
                compiler.specs.generateRandomSampleOfSize(transducer, sampleSize, RAND, (backtrack, finalState) -> {
                    final BacktrackingHead head = new BacktrackingHead(
                            backtrack, transducer.getFinalEdge(finalState));
                    final IntSeq in = head.randMatchingInput(RAND);
                    final IntSeq out = head.collect(in, compiler.specs.minimal());
                    logs.accept(in.toStringLiteral() + ":" + out.toStringLiteral());
                }, x -> {
                });
                return null;
            } else if (mode.equals("of_length")) {
                final int maxLength = param;
                compiler.specs.generateRandomSampleBoundedByLength(transducer, maxLength, 10, RAND,
                        (backtrack, finalState) -> {
                            final BacktrackingHead head = new BacktrackingHead(
                                    backtrack, transducer.getFinalEdge(finalState));
                            final IntSeq in = head.randMatchingInput(RAND);
                            final IntSeq out = head.collect(in, compiler.specs.minimal());
                            logs.accept(in.toStringLiteral() + ":" + out.toStringLiteral());
                        }, x -> {
                        });
                return null;
            } else {
                return "Choose one of the generation modes: 'of_size' or 'of_length'";
            }

        } catch (WeightConflictingToThirdState | NumberFormatException e) {
            return e.toString();
        }
    };
    public static final ReplCommand<String> REPL_VISUALIZE = (compiler, logs, debug, args) -> {
        try {
            compiler.visualize(args);
            return null;
        } catch (CompilationError e) {
            return e.toString();
        }
    };

    public static class Repl {
        private static class CmdMeta<Result> {
            final ReplCommand<Result> cmd;
            final String help;

            private CmdMeta(ReplCommand<Result> cmd, String help) {
                this.cmd = cmd;
                this.help = help;
            }
        }

        private final HashMap<String, CmdMeta<String>> commands = new HashMap<>();
        private final OptimisedLexTransducer.OptimisedHashLexTransducer compiler;

        public ReplCommand<String> registerCommand(String name, String help, ReplCommand<String> cmd) {
            final CmdMeta<String> prev = commands.put(name, new CmdMeta<>(cmd, help));
            return prev == null ? null : prev.cmd;
        }

        public Repl(OptimisedLexTransducer.OptimisedHashLexTransducer compiler) {
            this.compiler = compiler;
            registerCommand("exit", "Exits REPL", (a, b, d, c) -> "");
            registerCommand("load", "Loads source code from file", REPL_LOAD);
            registerCommand("pipes", "Lists all currently defined pipelines", REPL_LIST_PIPES);
            registerCommand("run", "Runs pipeline for the given input", REPL_RUN);
            registerCommand("ls", "Lists all currently defined transducers", REPL_LIST);
            registerCommand("size", "Size of transducer is the number of its states", REPL_SIZE);
            registerCommand("equal",
                    "Tests if two DETERMINISTIC transducers are equal. Does not work with nondeterministic ones!",
                    REPL_EQUAL);
            registerCommand("is_det", "Tests whether transducer is deterministic", REPL_IS_DETERMINISTIC);
            registerCommand("export", "Exports transducer to STAR (Subsequential Transducer ARchie) binary file",
                    REPL_EXPORT);
            registerCommand("eval", "Evaluates transducer on requested input", REPL_EVAL);
            registerCommand("rand_sample", "Generates random sample of input:output pairs produced by ths transducer",
                    REPL_RAND_SAMPLE);
            registerCommand("vis", "Visualizes transducer as a graph", REPL_VISUALIZE);
        }

        public String run(String line, Consumer<String> log, Consumer<String> debug) {
            if (line.startsWith(":")) {
                final int space = line.indexOf(' ');
                final String firstWord;
                final String remaining;
                if (space >= 0) {
                    firstWord = line.substring(1, space);
                    remaining = line.substring(space + 1);
                } else {
                    firstWord = line.substring(1);
                    remaining = "";
                }
                if (firstWord.startsWith("?")) {
                    final String noQuestionmark = firstWord.substring(1);
                    if (noQuestionmark.isEmpty()) {
                        final StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, CmdMeta<String>> cmd : commands.entrySet()) {
                            final String name = cmd.getKey();
                            sb.append(":").append(name).append("\t").append(cmd.getValue().help).append("\n");
                        }
                        return sb.toString();
                    } else {
                        final CmdMeta<String> cmd = commands.get(noQuestionmark);
                        return cmd.help;
                    }
                } else {
                    final CmdMeta<String> cmd = commands.get(firstWord);
                    return cmd.cmd.run(compiler, log, debug, remaining);
                }

            } else {
                try {
                    compiler.parseREPL(CharStreams.fromString(line));
                    return null;
                } catch (CompilationError | NoViableAltException | EmptyStackException e) {
                    return e.toString();
                }
            }
        }
    }

    public static void loop(Repl repl) throws IOException {
        try (final BufferedReader sc = new BufferedReader(new InputStreamReader(System.in))) {
            System.err.println("Solomonoff interactive console. Type :? for help");
            System.err.flush();
            while (true) {
                System.err.print(">");
                System.err.flush();
                final String line = sc.readLine();
                if(line==null||line.equals(":exit"))break;
                final String out = repl.run(line,System.out::println,System.err::println);
                if(out!=null)System.out.println(out);
            }
        }
    }
    
    public static void evalFileContent(Repl repl, File file) throws IOException {
//        Scanner scanner = new Scanner(file);
//        Pattern pattern = Pattern.compile("(?<!\\)\s*\n|(?<!\\)\s*\r\n|\s*;");
//        scanner.useDelimiter(pattern);
        FileReader _file = new FileReader(file);
        BufferedReader reader = new BufferedReader(_file);
        String line = reader.readLine();

        while (line != null) {
            final String out = repl.run(line,System.out::println,System.err::println);
            if(out!=null)System.out.println(out);

            line = reader.readLine();
        }

        reader.close();
        _file.close();
    }
}
