package net.alagris;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

public class TomlParser {
    public static class Config {
        Source[] source;
        Target[] target;
        String cacheLocation;
        boolean cashing;
        
        public Config() {
            cacheLocation = "bin/cache/";
            cashing = true;
        }

        public static Config parse(File configFile) throws CLIException.BuildFileException {
            if (!configFile.exists()) {
                return null;
            }
            final Toml toml = new Toml().read(configFile);
            Config config;
            config = toml.to(Config.class);
            return config;
        }
    }

    public static class Target {
        String id;
        String out;
    }

    public static class Source {
        String path;
        String algorithm; //"RPNI", "OSTIA"
        String name;//name of produced transducer
    }
}
