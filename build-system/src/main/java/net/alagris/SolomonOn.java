package net.alagris;

import java.io.IOException;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;

import static net.alagris.OptimisedLexTransducer.makeEmptyExternalPipelineFunction;

@Command(name = "solomonoff", mixinStandardHelpOptions = true,
        description = "Solomonoff compiler")
class Compiler implements Callable<Integer> {

    @Parameters(index = "0", defaultValue = "build", description = "\nbuild\nrun\ninteractive")
    private String command;

    @Option(names = {"-f", "--file"}, description = "file to run")
    private String inputFile;
    
    @Option(names = {"-b", "--build-file"}, defaultValue = "build.toml", description = "default is build.toml")
    private String buildFile;

    @Option(names = {"-n", "--no-minimization"}, description = "disable minimization")
    private boolean disableMinimimization;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        OptimisedLexTransducer.OptimisedHashLexTransducer compiler = null;

        if (command.equals("interactive")) {
            compiler = new OptimisedLexTransducer.OptimisedHashLexTransducer(
                    (!disableMinimimization),
                    0,
                    Integer.MAX_VALUE,
                    makeEmptyExternalPipelineFunction());
        } else if (command.equals("run") || command.equals("build")) {
            compiler = new OptimisedLexTransducer.OptimisedHashLexTransducer(
                    0,
                    Integer.MAX_VALUE,
                    false);
            SolomonoffBuildSystem.run(buildFile, compiler.specs, i -> {
                compiler.specs.pseudoMinimize(i);
                return i;
            });

        } else {
            System.err.println("Invalid command. Try -h for help");
            return 1;
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
