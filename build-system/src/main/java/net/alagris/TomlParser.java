package net.alagris;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TomlParser {
    public static class Config {
        String projectName;
        String version;
        String remote_repo;
        String private_key;
        String local_repo;
        List<SourceFile> source;
        List<Package> pkg;
//        Target[] target;
        String cache_location;
        boolean caching_write;
        boolean caching_read;
        
        
        public Config() {
            cache_location = "bin/";
            caching_write = true;
            caching_read = true;
            local_repo = Paths.get(System.getProperty("user.home"),".Solomonoff").toString();
        }

        public static Config parse(File configFile) throws IOException, CLIException.PKFileException {
            final Toml toml = new Toml().read(configFile);
            Config config;
            config = toml.to(Config.class);
//            if (config.projectName == null) {
//                throw new IllegalArgumentException( "projectName is missing from " + configFile);
//            }
            if (config.source != null) {
                String buildFilePath = configFile.getAbsoluteFile().getParent();
                for (SourceFile src : config.source) {
                    if (!Paths.get(src.path).isAbsolute()) {
                        src.path = Paths.get(buildFilePath, src.path).toString();
                    }
                }
            }
            if (config.pkg != null) {
                final String repoPath = Paths.get(config.local_repo).toAbsolutePath().toString();
                final String buildFilePath = configFile.getAbsoluteFile().getParent();
                for (Package pkg : config.pkg) {
                    if (pkg.path == null) {
                        pkg.path = Paths.get(repoPath, pkg.name + "-" + pkg.version + ".zip").toString();
                    } else if (!Paths.get(pkg.path).isAbsolute()) {
                        pkg.path = Paths.get(buildFilePath, pkg.path).toString();
                    }
                }
            }
            Files.createDirectories(Paths.get(config.cache_location));
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

    public static class SourceFile {
        String path;
        String algorithm; //"RPNI", "OSTIA"
        String name;//name of produced transducer

        public SourceFile() {}

        public SourceFile(String path) {
            this.path = path;
        }
    }

    public static class Package {
        String name;
        String version;
        String public_key;
        boolean verify_signature;
        String path;

        public Package() {
            this.verify_signature = true;
        }

        public Package(String name, String version) {
            this.name = name;
            this.version = version;
            this.verify_signature = false;
        }

        public Package(String name, String version, String public_key) {
            this.name = name;
            this.version = version;
            this.verify_signature = true;
            this.public_key = public_key;
        }
    }

}
