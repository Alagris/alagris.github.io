package com.compiler.Compiler.controllers;
        import net.alagris.*;
        import org.antlr.v4.runtime.CharStreams;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import javax.servlet.http.HttpSession;
        import java.io.BufferedOutputStream;
        import java.io.DataOutputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.*;
        import java.util.function.Consumer;


@RestController
public class NewRestController {

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
            Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> r = compiler.getOptimisedTransducer(args);
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
            final Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> graph = compiler.getOptimisedTransducer(transducerName);
            if (graph == null)
                return "Transducer '" + transducerName + "' not found!";
            final IntSeq input = ParserListener.parseCodepointOrStringLiteral(transducerInput);
            final IntSeq output = compiler.specs.evaluate(graph, input);
            final long evaluationTook = System.currentTimeMillis() - evaluationBegin;
            debug.accept("Took " + evaluationTook + " miliseconds");
            return output == null ? "No match!" : output.toStringLiteral();
        } catch (CompilationError.WeightConflictingToThirdState e) {
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
        final LexUnicodeSpecification.LexPipeline<HashMapIntermediateGraph.N<Pos, LexUnicodeSpecification.E>, HashMapIntermediateGraph<Pos, LexUnicodeSpecification.E, LexUnicodeSpecification.P>> pipeline = compiler
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
        LexUnicodeSpecification.Var<HashMapIntermediateGraph.N<Pos, LexUnicodeSpecification.E>, HashMapIntermediateGraph<Pos, LexUnicodeSpecification.E, LexUnicodeSpecification.P>> g = compiler
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
            Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> r = compiler.getOptimisedTransducer(args);
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
            Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> r1 = compiler.getOptimisedTransducer(transducer1);
            Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> r2 = compiler.getOptimisedTransducer(transducer2);
            if (r1 == null)
                return "No such transducer '" + transducer1 + "'!";
            if (r2 == null)
                return "No such transducer '" + transducer2 + "'!";
            final Specification.AdvAndDelState<Integer, IntQueue> counterexample = compiler.specs.areEquivalent(r1, r2);
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
            final Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> transducer = compiler.getOptimisedTransducer(transducerName);
            if (mode.equals("of_size")) {
                final int sampleSize = param;
                compiler.specs.generateRandomSampleOfSize(transducer, sampleSize, RAND, (backtrack, finalState) -> {
                    final LexUnicodeSpecification.BacktrackingHead head = new LexUnicodeSpecification.BacktrackingHead(
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
                            final LexUnicodeSpecification.BacktrackingHead head = new LexUnicodeSpecification.BacktrackingHead(
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

        } catch (CompilationError.WeightConflictingToThirdState | NumberFormatException e) {
            return e.toString();
        }
    };
    public static final ReplCommand<String> REPL_VISUALIZE = (compiler, logs, debug, args) -> {
        try {
            final Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> r = compiler.getOptimisedTransducer(args);
            LearnLibCompatibility.visualize(r);
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

        private final HashMap<String, Repl.CmdMeta<String>> commands = new HashMap<>();
        private final OptimisedLexTransducer.OptimisedHashLexTransducer compiler;

        public ReplCommand<String> registerCommand(String name, String help, ReplCommand<String> cmd) {
            final Repl.CmdMeta<String> prev = commands.put(name, new Repl.CmdMeta<>(cmd, help));
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
                        for (Map.Entry<String, Repl.CmdMeta<String>> cmd : commands.entrySet()) {
                            final String name = cmd.getKey();
                            sb.append(":").append(name).append("\t").append(cmd.getValue().help).append("\n");
                        }
                        return sb.toString();
                    } else {
                        final Repl.CmdMeta<String> cmd = commands.get(noQuestionmark);
                        return cmd.help;
                    }
                } else {
                    final Repl.CmdMeta<String> cmd = commands.get(firstWord);
                    return cmd.cmd.run(compiler, log, debug, remaining);
                }

            } else {
                try {
                    compiler.parseREPL(CharStreams.fromString(line));
                    return null;
                } catch (CompilationError | EmptyStackException e) {
                    return e.toString();
                }
            }
        }
    }

    @PostMapping("/compile")
    public String compile(HttpSession httpSession, @RequestBody String text){
        httpSession.setAttribute("code",text);
        Repl repl = (Repl) httpSession.getAttribute("repl");
        if(repl==null){
            try {
                repl = new Repl(new OptimisedLexTransducer.OptimisedHashLexTransducer(0,Integer.MAX_VALUE,true));
            } catch (Exception compilationError) {
                return compilationError.getMessage();
            }
            httpSession.setAttribute("repl",repl);
        }
        try {
            repl.compiler.parse(CharStreams.fromString(text));
        } catch (CompilationError compilationError) {
            repl.compiler.listener.automata.clear();
            return compilationError.toString();
        }
        return "";
    }

    @PostMapping("/repl")
    public String repl(HttpSession httpSession, @RequestBody String line){
        Repl repl = (Repl) httpSession.getAttribute("repl");
        if(repl==null){
            try {
                repl = new Repl(new OptimisedLexTransducer.OptimisedHashLexTransducer(0,Integer.MAX_VALUE,true));
            } catch (Exception compilationError) {
                return compilationError.getMessage();
            }
            httpSession.setAttribute("repl",repl);
        }

        try {
            final StringBuilder out = new StringBuilder();
            final String result = repl.run(line, s -> out.append(s).append('\n'), s->{});
            out.append(result);
            return out.toString();
        }catch (Exception e){
            return e.toString();
        }

    }

}