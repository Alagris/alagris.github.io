package net.alagris;

import net.alagris.TomlParser.*;

import java.io.*;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;


@Command(name = "solomonoff", mixinStandardHelpOptions = true, description = "Solomonoff compiler")
class SolomonOn implements Callable<Integer> {

    private Config config;
    private InputStream scriptStream;

    @Parameters(index = "0", description =
            "${COMPLETION-CANDIDATES}\n\n"
            + "check       - test if buildable\n"
            + "build       - binaries building\n"
            + "run         - evaluate built binaries (interactive session or from script)\n"
            + "interactive - empty interactive session - REPL\n"
            + "export      - package building\n"
            + "clean       - binaries removing\n"
            +"\n\n"
    )
    private Mode mode;

    @Parameters(index = "1", defaultValue = "", description = "function to run")
    private String functionName;

    @Option(names = {"-f", "--script-file"}, description = "script file to evaluate")
    private File scriptFile;

    @Option(names = {"-b", "--build-file"}, defaultValue = "build.toml", description = "default is build.toml")
    private File buildFile;

    @Option(names = {"-n", "--no-minimization"}, description = "disable minimization")
    private boolean disableMinimimization;

    @Option(names = {"-i", "--interactive"},
            description = "Enable interactive mode")
    private boolean interactive;

    @Option(names = {"-u", "--volatile"}, description = "Do not save compiled files")
    private boolean noCaching;

    @Option(names = {"-g", "--ignore-saved"}, description = "Ignore previously saved binaries")
    private boolean ignoreCache;

    @Option(names = {"-@", "--stdin"}, description = "set stdin as script file")
    private  boolean setStdin;

    @Option(names = {"-l", "--install-local-pkg"}, description = "install from local drive")
    private  boolean localInstall;

    private enum Mode { run, build, interactive, clean, export, check }

    private void updateConfig() throws FileNotFoundException {
        config.caching_write = !noCaching;
        config.caching_read = !ignoreCache;
        if (scriptFile != null) {
            scriptStream = new FileInputStream(scriptFile);
        }
        if (setStdin) {
            scriptStream = System.in;
        }
    }

    private void clean() {
        final File cache = new File(config.cache_location);
        if (!cache.exists()) {
            return;
        }
        File[] files = cache.listFiles(File::isFile);
        for (File f : files) {
            f.delete();
        }
    }
    
    @Override
    public Integer call() throws Exception {
        Compiler compiler;
        if (buildFile.exists()) {
            buildFile = buildFile.getAbsoluteFile();
            try {
                config = TomlParser.Config.parse(buildFile);
            } catch (Exception e) {
                return 7;
            }
            try {
                updateConfig();
            } catch (FileNotFoundException e) {
                return 2;
            }
        }
        Executor executor;

        switch (mode) {
            case export:
                try {
                    Packages.buildPackage(config);
                } catch (Exception e) {
                    return 8;
                }
                break;
            case check:
                if (config == null) {
                    System.err.println("Cannot find " + buildFile);
                    return 1;
                }
                config.caching_write = false;
                compiler = new Compiler(config);
                try {
                    compiler.compile();
                } catch (Exception e) {
                    return 5;
                }
                break;
            case build:
                if (config == null) {
                   System.err.println("Cannot find " + buildFile);
                   return 1;
                }
                compiler = new Compiler(config);
                try {
                    compiler.compile();
                } catch (Exception e) {
                    return 5;
                }
                break;
            case run:
                if (functionName.isEmpty()) {
                    compiler = new Compiler(config);
                    if (config == null) {
                        System.err.println("Cannot find " + buildFile);
                        return 1;
                    }
                    try {
                        compiler.compile();
                    } catch (Exception e) {
                        return 6;
                    }
                } else {
                    compiler = new Compiler(new Config());
                    compiler.loadBinary(functionName);
                }
                executor = new Executor(compiler.getTransducer(), buildFile.getParentFile());
                if (scriptFile != null) {
                    if (scriptStream != null) {
                        executor.evalFileContent(scriptStream);
                    } else {
                        System.err.println("Invalid file " + scriptFile);
                        return 2;
                    }
                }
                if (interactive || scriptFile == null) {
                    executor.loop();
                }
                break;
            case interactive:
                compiler = new Compiler(new Config());
                executor = new Executor(compiler.getTransducer(), buildFile.getParentFile());
                if (scriptStream != null) {
                    executor.evalFileContent(scriptStream);
                }
                executor.loop();
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
