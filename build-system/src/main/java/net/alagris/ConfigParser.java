package net.alagris;

import com.moandjiezana.toml.Toml;

import java.io.File;

public class ConfigParser {
    public static class Config {
        Source[] source;
        Target[] target;
    }

    public static class Target {
        String name;
        String path;
    }

    public static class Source {
        String path;
        String algorithm; //"RPNI", "OSTIA"
        String name;//name of produced transducer
    }

    public static Config parse(File buildFile) throws CLIException.BuildFileException {
        if (!buildFile.exists()) {
            throw new CLIException.BuildFileException(buildFile.toString());
        }
        final Toml toml = new Toml().read(buildFile);
        Config config;
        config = toml.to(Config.class);
        return config;
    }

}
