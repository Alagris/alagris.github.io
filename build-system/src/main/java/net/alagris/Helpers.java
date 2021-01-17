package net.alagris;

import java.io.File;

public class Helpers {
    public static void clean(TomlParser.Config config) {
        final File cache = new File(config.cache_location);
        if (!cache.exists()) {
            return;
        }
        File[] files = cache.listFiles(File::isFile);
        for (File f : files) {
            f.delete();
        }
    }
}
