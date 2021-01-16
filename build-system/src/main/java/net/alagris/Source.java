package net.alagris;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Source {
    String path;
    String algorithm;
    String name;
    CharStream data;

    public Source(String path, CharStream data) {
        this.path = path;
        this.data = data;
    }
    
    public static List<Source> fromSourceFiles(List<TomlParser.SourceFile> sourceFiles) throws IOException {
        List<Source> sources = new LinkedList<Source>();
        for (TomlParser.SourceFile s : sourceFiles) {
            sources.add(new Source(s.path, CharStreams.fromFileName(s.path)));
        }
        return sources;
    }

    public static List<Source> fromPackages(List<TomlParser.Package> packages) throws IOException {
        List<Source> sources = new LinkedList<Source>();
        if (packages == null) {
            return sources;
        }
        for (TomlParser.Package p : packages) {
            System.err.println("Reading " + p.path);
            System.err.println("Verifying " + p.path);
            ZipFile zip = new ZipFile(p.path);
            ZipInputStream zis = new ZipInputStream(new FileInputStream(p.path));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                sources.add(new Source(p.path, CharStreams.fromStream(zip.getInputStream(zipEntry))));
            }
            zis.closeEntry();
            zis.close();
        }

        return sources;
    }
}
