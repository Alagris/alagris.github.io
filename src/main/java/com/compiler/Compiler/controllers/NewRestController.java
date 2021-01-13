package com.compiler.Compiler.controllers;

import net.alagris.*;
import org.antlr.v4.runtime.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import net.automatalib.graphs.Graph;
import net.automatalib.serialization.dot.GraphDOT;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;


@RestController
public class NewRestController {

    private final static Random RAND = new Random();

    public interface ReplCommand<Result> {
        Result run(HttpSession httpSession, OptimisedLexTransducer.OptimisedHashLexTransducer compiler, Consumer<String> log, Consumer<String> debug, String args) throws Exception;
    }


    public static final ReplCommand<String> REPL_LIST = (httpSession, compiler, logs, debug, args) -> compiler.specs.variableAssignments
            .keySet().toString();
    public static final ReplCommand<String> REPL_SIZE = (httpSession, compiler, logs, debug, args) -> {
        Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> r = compiler.getOptimisedTransducer(args);
        return r == null ? "No such function!" : String.valueOf(r.size());
    };
    public static final ReplCommand<String> REPL_EVAL = (httpSession, compiler, logs, debug, args) -> {
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
    };
    public static final ReplCommand<String> REPL_RUN = (httpSession, compiler, logs, debug, args) -> {
        final String[] parts = args.split("\\s+", 2);
        if (parts.length != 2)
            return "Two arguments required 'transducerName' and 'transducerInput' but got " + Arrays.toString(parts);
        final String pipelineName = parts[0].trim();
        if (!pipelineName.startsWith("@")) {
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
    public static final ReplCommand<String> REPL_EXPORT = (httpSession, compiler, logs, debug, args) -> {
        LexUnicodeSpecification.Var<HashMapIntermediateGraph.N<Pos, LexUnicodeSpecification.E>, HashMapIntermediateGraph<Pos, LexUnicodeSpecification.E, LexUnicodeSpecification.P>> g = compiler
                .getTransducer(args);
        try (FileOutputStream f = new FileOutputStream(args + ".star")) {
            compiler.specs.compressBinary(g.graph, new DataOutputStream(new BufferedOutputStream(f)));
            return null;
        }
    };

    public static final ReplCommand<String> REPL_IS_DETERMINISTIC = (httpSession, compiler, logs, debug, args) -> {
        Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> r = compiler.getOptimisedTransducer(args);
        if (r == null)
            return "No such function!";
        return r.isDeterministic() == null ? "true" : "false";
    };
    public static final ReplCommand<String> REPL_LIST_PIPES = (httpSession, compiler, logs, debug, args) -> {
        return Specification.fold(compiler.specs.pipelines.keySet(), new StringBuilder(),
                (pipe, sb) -> sb.append("@").append(pipe).append(", ")).toString();
    };
    public static final ReplCommand<String> REPL_EQUAL = (httpSession, compiler, logs, debug, args) -> {
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
    };
    public static final ReplCommand<String> REPL_RAND_SAMPLE = (httpSession, compiler, logs, debug, args) -> {
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

    };
    public static final ReplCommand<String> REPL_VISUALIZE = (httpSession, compiler, logs, debug, args) -> {
        final Specification.RangedGraph<Pos, Integer, LexUnicodeSpecification.E, LexUnicodeSpecification.P> r = compiler.getOptimisedTransducer(args);
        LearnLibCompatibility.visualize(r);
        return null;
    };

    public static final ReplCommand<String> REPL_CLEAR = (httpSession, compiler, logs, debug, args) -> {
        StringBuilder history = (StringBuilder) httpSession.getAttribute("repl_history");
        if (history == null) {
            history = new StringBuilder();
            httpSession.setAttribute("repl_history", history);
        }
        history.setLength(0);
        return null;
    };

    public static final ReplCommand<String> REPL_UNSET = (httpSession, compiler, logs, debug, args) -> {
        compiler.specs.variableAssignments.remove(args);
        return null;
    };
    public static final ReplCommand<String> REPL_RESET = (httpSession, compiler, log, debug, args) -> {
        compiler.specs.variableAssignments.keySet().removeIf(k -> !(k.equals("Σ") || k.equals("#") || k.equals("∅") || k.equals("ε") || k.equals(".")));
        return null;
    };
    public static final ReplCommand<String> REPL_LOAD = (httpSession, compiler, log, debug, args) -> {
        String code = (String) httpSession.getAttribute("code");
        if (code != null) compiler.parse(CharStreams.fromString(code));
        return null;
    };
    public static final ReplCommand<String> REPL_VIS = (httpSession, compiler, log, debug, args) -> buildGraph(httpSession, args.trim());

    public static class Repl {
        private static class CmdMeta<Result> {
            final ReplCommand<Result> cmd;
            final String help;
            final String template;

            private CmdMeta(ReplCommand<Result> cmd, String help, String template) {
                this.cmd = cmd;
                this.help = help;
                this.template = template;
            }
        }

        private final HashMap<String, Repl.CmdMeta<String>> commands = new HashMap<>();
        public final OptimisedLexTransducer.OptimisedHashLexTransducer compiler;

        public ReplCommand<String> registerCommand(String name, String help, String template, ReplCommand<String> cmd) {
            final Repl.CmdMeta<String> prev = commands.put(name, new Repl.CmdMeta<>(cmd, help, template));
            return prev == null ? null : prev.cmd;
        }

        public Repl(OptimisedLexTransducer.OptimisedHashLexTransducer compiler) {
            this.compiler = compiler;
            registerCommand("clear", "Clears REPL console", ":clear", REPL_CLEAR);
            registerCommand("vis", "Shows graph diagram of automaton", ":vis [ID]", REPL_VIS);
            registerCommand("unset", "Deletes a variable", ":unset [ID]", REPL_UNSET);
            registerCommand("load", "Loads source code", ":load", REPL_LOAD);
            registerCommand("reset", "Removes all user defined variables", ":reset", REPL_RESET);
            registerCommand("pipes", "Lists all currently defined pipelines", ":pipes", REPL_LIST_PIPES);
            registerCommand("run", "Runs pipeline for the given input", ":run [ID] [STRING]", REPL_RUN);
            registerCommand("ls", "Lists all currently defined transducers", ":ls", REPL_LIST);
            registerCommand("size", "Size of transducer is the number of its states", ":size [ID]", REPL_SIZE);
            registerCommand("equal",
                    "Tests if two DETERMINISTIC transducers are equal. Does not work with nondeterministic ones!",
                    ":equal [ID] [ID]",
                    REPL_EQUAL);
            registerCommand("is_det", "Tests whether transducer is deterministic", ":is_det [ID]", REPL_IS_DETERMINISTIC);

            registerCommand("eval", "Evaluates transducer on requested input", ":eval [ID] [STRING]", REPL_EVAL);
            registerCommand("rand_sample", "Generates random sample of input:output pairs produced by ths transducer",
                    ":rand_sample [ID] [of_size/of_length] [NUM]",
                    REPL_RAND_SAMPLE);
            registerCommand("?", "Prints help and usage examples", ":? [COMMAND (optional)]", (httpSession, repl, logs, debug, args) -> {
                args = args.trim();
                if (args.isEmpty()) {
                    final StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, Repl.CmdMeta<String>> cmd : commands.entrySet()) {
                        sb.append(cmd.getValue().template).append("\n    ").append(cmd.getValue().help).append('\n');
                    }
                    return sb.toString();
                } else {
                    final Repl.CmdMeta<String> cmd = commands.get(args);
                    return cmd.help + ". Usage:\n        " + cmd.template + '\n';
                }
            });
            compiler.parser.removeErrorListeners();
            compiler.parser.addErrorListener(new BaseErrorListener() {
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    throw new ParseError(line,charPositionInLine,msg,e);
                }
            });
            compiler.parser.setErrorHandler(new BailErrorStrategy());
        }

        public String run(HttpSession httpSession, String line, Consumer<String> log, Consumer<String> debug) throws Exception {
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
                final Repl.CmdMeta<String> cmd = commands.get(firstWord);
                return cmd.cmd.run(httpSession, compiler, log, debug, remaining);
            } else {
                compiler.parse(CharStreams.fromString(line));
                return null;
            }
        }
    }

    public static class ParseError extends RuntimeException{

        public final int line;
        public final int charPositionInLine;
        public final String msg;
        public final RecognitionException e;

        public ParseError(int line, int charPositionInLine, String msg, RecognitionException e) {
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            this.msg = msg;
            this.e = e;
        }
    }

    @PostMapping("/upload_code")
    public void uploadCode(HttpSession httpSession, @RequestBody String text) {
        httpSession.setAttribute("code", text);
    }

    public ReplResponse compile(HttpSession httpSession, @RequestBody String text) {
        uploadCode(httpSession, text);
        return repl(httpSession, ":load");
    }

    public static class ReplResponse {
        public boolean wasError;
        public String output;
        public int col;
        public int row;

        public ReplResponse(String output) {
            this.wasError = false;
            this.output = output;
            col = -1;
            row = -1;
        }

        public ReplResponse(Throwable exception) {
            if (exception.getClass().equals(RuntimeException.class) && exception.getCause() != null) {
                exception = exception.getCause();
            }
            this.wasError = true;
            output = exception.toString();
            col = -1;
            row = -1;
            if (exception instanceof CompilationError.DuplicateFunction) {
                CompilationError.DuplicateFunction e = (CompilationError.DuplicateFunction) exception;
                output = "Variable " + e.getName() + " already exists! You cannot redefine it unless you either consume it or run ':unset " + e.getName() + "' command in REPL.";
                if(e.secondDeclaration!=null) {
                    col = e.secondDeclaration.getColumn();
                    row = e.secondDeclaration.getLine();
                }
            } else if (exception instanceof CompilationError.MissingFunction) {
                CompilationError.MissingFunction e = (CompilationError.MissingFunction) exception;
                output = "Variable " + e.id + " not found!";
                if(e.pos!=null) {
                    col = e.pos.getColumn();
                    row = e.pos.getLine();
                }
            } else if (exception instanceof CompilationError.KleeneNondeterminismException) {
                CompilationError.KleeneNondeterminismException e = (CompilationError.KleeneNondeterminismException) exception;
                output = e.toString();
                if(e.kleenePos!=null) {
                    col = e.kleenePos.getColumn();
                    row = e.kleenePos.getLine();
                }
            } else if (exception instanceof CompilationError.IllegalCharacter) {
                CompilationError.IllegalCharacter e = (CompilationError.IllegalCharacter) exception;
                output = e.toString();
                if(e.pos!=null) {
                    col = e.pos.getColumn();
                    row = e.pos.getLine();
                }
            } else if(exception instanceof NoViableAltException) {
                NoViableAltException e = (NoViableAltException) exception;
                output = e.toString();
                if(e.getStartToken()!=null) {
                    col = e.getStartToken().getCharPositionInLine();
                    row = e.getStartToken().getLine();
                }
            }else if(exception instanceof CompilationError.UndefinedExternalFunc) {
                CompilationError.UndefinedExternalFunc e = (CompilationError.UndefinedExternalFunc) exception;
                output = e.toString();
                if(e.pos!=null) {
                    col = e.pos.getColumn();
                    row = e.pos.getLine();
                }
            }else if(exception instanceof CompilationError.TypecheckException) {
                CompilationError.TypecheckException e = (CompilationError.TypecheckException) exception;
                if(e.typePos!=null) {
                    col = e.typePos.getColumn();
                    row = e.typePos.getLine();
                }
                output = "[line " + row + ":" + col + "] Function "+e.name+" does not conform to type";
            }else if(exception instanceof RecognitionException) {
                RecognitionException e = (RecognitionException) exception;
                output = e.toString();
                if(e.getOffendingToken()!=null) {
                    col = e.getOffendingToken().getCharPositionInLine();
                    row = e.getOffendingToken().getLine();
                }
            }else if(exception instanceof ParseError) {
                ParseError e = (ParseError) exception;
                col = e.charPositionInLine;
                row = e.line;
                output = "[line " + row + ":" + col + "] "+e.msg+" ("+e.e.toString()+")";
            }
        }
    }

    @PostMapping("/repl")
    public ReplResponse repl(HttpSession httpSession, @RequestBody String line) {
        Repl repl = (Repl) httpSession.getAttribute("repl");
        if (repl == null) {
            try {
                repl = new Repl(new OptimisedLexTransducer.OptimisedHashLexTransducer(0, Integer.MAX_VALUE, true));
            } catch (Exception compilationError) {
                return new ReplResponse(compilationError);
            }
            httpSession.setAttribute("repl", repl);
        }
        StringBuilder history = (StringBuilder) httpSession.getAttribute("repl_history");
        if (history == null) {
            history = new StringBuilder();
            httpSession.setAttribute("repl_history", history);
        }
        if (history.length()>0 && history.charAt(history.length()-1)!='\n') history.append('\n');
        history.append("> ").append(line);
        if (!line.endsWith("\n")) history.append('\n');
        try {
            final StringBuilder out = new StringBuilder();
            repl.compiler.specs.setVariableRedefinitionCallback((var, var1, pos) -> out.append("Warning! Variable ").append(var.name).append(" redefined!\n"));
            final String result = repl.run(httpSession, line, s -> out.append(s).append('\n'), s -> {
            });
            if (result != null) out.append(result);
            history.append(out);
            return new ReplResponse(out.toString());
        } catch (Exception e) {
            final ReplResponse r = new ReplResponse(e);
            history.append(r.output);
            return r;
        }

    }


    @PostMapping("/list_automata")
    public String listAutomata(HttpSession httpSession) {
        Repl repl = (Repl) httpSession.getAttribute("repl");
        if (repl == null) {
            try {
                repl = new Repl(new OptimisedLexTransducer.OptimisedHashLexTransducer(0, Integer.MAX_VALUE, true));
            } catch (Exception compilationError) {
                return compilationError.getMessage();
            }
            httpSession.setAttribute("repl", repl);
        }
        StringBuilder sb = new StringBuilder("[");
        Iterator<LexUnicodeSpecification.Var<HashMapIntermediateGraph.N<Pos, LexUnicodeSpecification.E>, HashMapIntermediateGraph<Pos, LexUnicodeSpecification.E, LexUnicodeSpecification.P>>> iterator = repl.compiler.specs.iterateVariables();
        while (iterator.hasNext()) {
            final LexUnicodeSpecification.Var<HashMapIntermediateGraph.N<Pos, LexUnicodeSpecification.E>, HashMapIntermediateGraph<Pos, LexUnicodeSpecification.E, LexUnicodeSpecification.P>> transducer = iterator.next();
            sb.append("\"");
            sb.append(transducer.name);
            sb.append("\"");
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();

    }

    @PostMapping("/get_graph")
    public String getGraph(HttpSession httpSession, @RequestBody String name) {
        try {
            return buildGraph(httpSession, name);
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static String buildGraph(HttpSession httpSession, String name) throws Exception {
        Repl repl = (Repl) httpSession.getAttribute("repl");
        if (repl == null) {
            repl = new Repl(new OptimisedLexTransducer.OptimisedHashLexTransducer(0, Integer.MAX_VALUE, true));
            httpSession.setAttribute("repl", repl);
        }
        final LexUnicodeSpecification.Var<HashMapIntermediateGraph.N<Pos, LexUnicodeSpecification.E>, HashMapIntermediateGraph<Pos, LexUnicodeSpecification.E, LexUnicodeSpecification.P>> tr = repl.compiler.getTransducer(name);
        if (tr == null) return "ERR:NOT FOUND";

        final Graph<?, ?> graph = LearnLibCompatibility.intermediateAsGraph(tr.graph, Pos.NONE);
        final StringWriter writer = new StringWriter();
        GraphDOT.write(graph, writer);
        return writer.toString();

    }


    @PostMapping("/add_to_repl_history")
    public void addReplHistory(HttpSession httpSession, @RequestBody String content) {
        StringBuilder sb = (StringBuilder) httpSession.getAttribute("repl_history");
        if (sb == null) {
            sb = new StringBuilder();
            httpSession.setAttribute("repl_history", sb);
        }
        sb.append(content);
    }

}