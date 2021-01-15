package net.alagris;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TomlParser {
    public static class Config {
        String projectName;
        ArrayList<Source> source;
//        Target[] target;
        String cacheLocation;
        boolean caching_write;
        boolean caching_read;
        
        
        public Config() {
            cacheLocation = "bin/";
            caching_write = true;
            caching_read = true;
        }

        public static Config parse(File configFile) throws CLIException.BuildFileException, IOException {
            final Toml toml = new Toml().read(configFile);
            Config config;
            config = toml.to(Config.class);
//            if (config.projectName == null) {
//                throw new IllegalArgumentException( "projectName is missing from " + configFile);
//            }
            if (config.source != null) {
                String buildFilePath = configFile.getAbsoluteFile().getParent();
                for (Source src : config.source) {
                    if (!Paths.get(src.path).isAbsolute()) {
                        src.path = Paths.get(buildFilePath, src.path).toString();
                    }
                }
            }
//            if (config.target==null || config.target.length==0){
//                System.err.println("No targets specified. Defaulting to 'main'");
//                config.target = new Target[]{new Target("main","bin/main.star")};
//            }
            Files.createDirectories(Paths.get(config.cacheLocation));
            return config;
        }
    }

    public static class Target {
        String id;
        String destination;
        public Target(){
        }
        public Target(String id,String destination){
            this.id = id;
            this.destination = destination;
        }

    }

    public static class Source {
        String path;
        String algorithm; //"RPNI", "OSTIA"
        String name;//name of produced transducer

        public Source() {}

        public Source(String path) {
            this.path = path;
        }
    }
    
}
