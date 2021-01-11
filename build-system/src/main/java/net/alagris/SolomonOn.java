package net.alagris;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;


@Command(name = "solomonoff", mixinStandardHelpOptions = true,
        description = "Solomonoff compiler")
class Compiler implements Callable<Integer> {

    @Parameters(index = "0", defaultValue = "build", description = "${COMPLETION-CANDIDATES}")
    private Mode mode;

    @Option(names = {"-f", "--input-file"}, defaultValue = "__NONE__", description = "file to evaluate")
    private File inputFile;

    @Option(names = {"-b", "--build-file"}, defaultValue = "build.toml", description = "default is build.toml")
    private File buildFile;

    @Option(names = {"-n", "--no-minimization"}, description = "disable minimization")
    private boolean disableMinimimization;

    @Option(names = {"-i", "--interactive"},
            description = "Enable interactive mode")
    private boolean interactive;

    @Option(names = {"-u", "--volatile"}, description = "Do not save to a file")
    private boolean noBinary;

    private enum Mode { run, build, interactive }


    @Override
    public Integer call() throws Exception { // your business logic goes here...
        final OptimisedLexTransducer.OptimisedHashLexTransducer compiler =
                SolomonoffBuildSystem.getCompiler();

        SolomonoffBuildSystem.runCompiler(buildFile, compiler.specs, i -> {
            compiler.specs.pseudoMinimize(i);
            return i;
        });
        Evaluation.Repl repl;

        switch (mode) {
            case build:
                break;
            case run:
                SolomonoffBuildSystem.runCompiler(buildFile, compiler.specs, i -> {
                    compiler.specs.pseudoMinimize(i);
                    return i;
                });
                repl = new Evaluation.Repl(compiler);
                if (!inputFile.equals("__NONE__") || inputFile.isFile()) {
                    Evaluation.evalFileContent(repl, inputFile);
                } else {
                    return 1;
                }
                if (interactive || inputFile.equals("__NONE__")) {
                    Evaluation.loop(repl);
                }
                break;
            case interactive:
                repl = new Evaluation.Repl(compiler);
                if (interactive) {
                    Evaluation.loop(repl);
                }
        }
        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Compiler()).execute(args);
        System.exit(exitCode);
    }
}
