package net.alagris;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.jgrapht.graph.*;
import picocli.CommandLine;
import picocli.CommandLine.*;

import static net.alagris.OptimisedLexTransducer.makeEmptyExternalPipelineFunction;

@Command(name = "solomonoff", mixinStandardHelpOptions = true,
        description = "Solomonoff compiler")
class Compiler implements Callable<Integer> {

    @Parameters(index = "0", defaultValue = "run", description = "\nbuild\nrun")
    private String command;

    @Option(names = {"-f", "--file"}, description = "default is build.toml")
    private String file = "build.toml";

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        OptimisedLexTransducer.OptimisedHashLexTransducer compiler = null;
        try {
            compiler = new OptimisedLexTransducer.OptimisedHashLexTransducer(
                    System.getenv("NO_MINIMIZATION") == null,
                    0,
                    Integer.MAX_VALUE,
                    makeEmptyExternalPipelineFunction());
        } catch (CompilationError compilationError) {
            compilationError.printStackTrace();
        }


        final ReplInfrastruct.Repl repl = new ReplInfrastruct.Repl(compiler);

        try {
            ReplInfrastruct.loop(repl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Compiler()).execute(args);
        System.exit(exitCode);
    }
}
