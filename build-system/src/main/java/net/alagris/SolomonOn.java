package net.alagris;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import picocli.CommandLine;
import picocli.CommandLine.*;

import static net.alagris.OptimisedLexTransducer.makeEmptyExternalPipelineFunction;

@Command(name = "solomonoff", mixinStandardHelpOptions = true,
        description = "Solomonoff compiler")
class Compiler implements Callable<Integer> {

    @Parameters(index = "0", defaultValue = "build", description = "${COMPLETION-CANDIDATES}")
    private Mode mode;

    @Parameters(index = "1", description = "File to evaluate")
    private File file;

    @Option(names = {"-f", "--file"}, description = "file to run")
    private String inputFile;
    
    @Option(names = {"-b", "--build-file"}, defaultValue = "build.toml", description = "default is build.toml")
    private String buildFile;

    @Option(names = {"-n", "--no-minimization"}, description = "disable minimization")
    private boolean disableMinimimization;

    @Option(names = {"-i", "--interactive"},
            description = "Enable interactive mode. Useful with build or with run")
    private boolean interactive;

    private enum Mode { run, build, interactive }


    @Override
    public Integer call() throws Exception { // your business logic goes here...

        if (Mode.build.compareTo(mode) == 0) {
            OptimisedLexTransducer.OptimisedHashLexTransducer compiler =
                    new OptimisedLexTransducer.OptimisedHashLexTransducer(
                    0,
                    Integer.MAX_VALUE);

            SolomonoffBuildSystem.run(buildFile, compiler.specs, i -> {
                compiler.specs.pseudoMinimize(i);
                return i;
            });

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
