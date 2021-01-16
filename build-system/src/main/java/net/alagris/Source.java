package net.alagris;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Source {
    String path;
    String algorithm;
    String name;
    CharStream data;
    String extension;

    public Source(String path, CharStream data) {
        this.path = path;
        this.data = data;
        this.name = FilenameUtils.getBaseName(path);
        this.extension = FilenameUtils.getExtension(path);
    }

    public Source(String path, CharStream data, String name, String extension) {
        this.path = path;
        this.data = data;
        this.extension = extension;
        this.name = name;
    }

    public static List<Source> fromSourceFiles(List<TomlParser.SourceFile> sourceFiles)
            throws CLIException.MealyFileException {
        List<Source> sources = new LinkedList<Source>();
        if (sourceFiles == null) {
            return sources;
        }
        for (TomlParser.SourceFile s : sourceFiles) {
            try {
                sources.add(new Source(s.path, CharStreams.fromFileName(s.path)));
            } catch (IOException e) {
                throw new CLIException.MealyFileException(s.path);
            }
        }
        return sources;
    }

    public static List<Source> fromPackages(List<TomlParser.Package> packages)
            throws CLIException.BinFileException, IOException {
        List<Source> sources = new LinkedList<Source>();
        if (packages == null) {
            return sources;
        }
        for (TomlParser.Package p : packages)  {
            System.err.println("Reading " + p.path);
            System.err.println("Verifying " + p.path);
            ZipFile zip = null;
            try {
                zip = new ZipFile(p.path);
            } catch (IOException e) {
                throw new CLIException.BinFileException(p.path);
            }
            ZipInputStream zis = new ZipInputStream(new FileInputStream(p.path));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                sources.add(
                        new Source(
                        p.path,
                        CharStreams.fromStream(zip.getInputStream(zipEntry)),
                        FilenameUtils.getBaseName(zipEntry.getName()),
                        FilenameUtils.getExtension(zipEntry.getName())
                ));
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        }

        return sources;
    }
}
