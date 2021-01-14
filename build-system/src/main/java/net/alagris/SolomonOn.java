package net.alagris;

import net.alagris.TomlParser.*;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;


@Command(name = "solomonoff", mixinStandardHelpOptions = true,
        description = "Solomonoff compiler")
class SolomonOn implements Callable<Integer> {

    private Config config;
    private InputStream scriptStream;

    @Parameters(index = "0", description = "${COMPLETION-CANDIDATES}")
    private Mode mode;

    @Parameters(index = "1", defaultValue = "__NONE__", description = "function to run")
    private String function;

    @Option(names = {"-f", "--script-file"}, description = "script file to evaluate")
    private File scriptFile;

    @Option(names = {"-b", "--build-file"}, defaultValue = "build.toml", description = "default is build.toml")
    private File buildFile;

    @Option(names = {"-n", "--no-minimization"}, description = "disable minimization")
    private boolean disableMinimimization;

    @Option(names = {"-i", "--interactive"},
            description = "Enable interactive mode")
    private boolean interactive;

    @Option(names = {"-u", "--volatile"}, description = "Do not save any files")
    private boolean noCaching;

    @Option(names = {"-@", "--stdin"}, description = "set stdin as script file")
    private  boolean setStdin;

    private enum Mode { run, build, interactive, clean }

    private void updateConfig() throws FileNotFoundException {
        config.cashing = !noCaching;
        if (scriptFile != null) {
            scriptStream = new FileInputStream(scriptFile);
        }
        if (setStdin) {
            scriptStream = System.in;
        }
    }

    private void clean() {
        final File cache = new File(config.cacheLocation);
        if (!cache.exists()) {
            return;
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
        for (File f : files) {
            f.delete();
        }
    }
    
    @Override
    public Integer call() throws Exception {
        final OptimisedLexTransducer.OptimisedHashLexTransducer compiler = SolomonoffBuildSystem.getCompiler();
        if (buildFile.exists()) {
            config = TomlParser.Config.parse(buildFile);
            try {
                updateConfig();
            } catch (FileNotFoundException e) {
                return 2;
            }
        }
        Evaluation.Repl repl;

        switch (mode) {
            case build:
                if (config == null) {
                   System.err.println("Cannot find " + buildFile);
                   return 1;
                }
                SolomonoffBuildSystem.runCompiler(config, compiler);
                break;
            case run:
                if (config == null) {
                    System.err.println("Cannot find " + buildFile);
                    return 1;
                }
                SolomonoffBuildSystem.runCompiler(config, compiler);
                repl = new Evaluation.Repl(compiler);
                if (scriptFile != null) {
                    if (scriptStream != null) {
                        Evaluation.evalFileContent(repl, scriptStream);
                    } else {
                        return 2;
                    }
                }
                if (interactive || scriptFile == null) {
                    Evaluation.loop(repl);
                }
                break;
            case interactive:
                repl = new Evaluation.Repl(compiler);
                if (scriptStream != null) {
                    Evaluation.evalFileContent(repl, scriptStream);
                }
                Evaluation.loop(repl);
                break;
            case clean:
                clean();
        }
        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new SolomonOn()).execute(args);
        System.exit(exitCode);
    }
}
