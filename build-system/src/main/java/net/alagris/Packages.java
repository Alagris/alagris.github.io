package net.alagris;

import net.alagris.TomlParser.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Packages {
    public static boolean verifyPackage(TomlParser.Package pkg)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException,
            SignatureException, CLIException.PKFileException {
        if (pkg.verify_signature == true) {
            if (pkg.public_key == null) {
                throw new CLIException.PKFileException(pkg.name);
            }
            return Packages._verifyPackage(Paths.get(pkg.path), pkg.public_key);
        }
        return true;
    }

    private static boolean _verifyPackage(Path pkg, String pk)
            throws InvalidKeySpecException, SignatureException, NoSuchAlgorithmException, InvalidKeyException,
            IOException {
        byte[] pkgData = Files.readAllBytes(pkg);
        Path sigPath = Paths.get(pkg.toString() + ".sig");
        byte[] sigData = Files.readAllBytes(sigPath);
        return verifySignature(pkgData, sigData, pk);
    }

    private static byte[] sign(String data, String keyFile)
            throws InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException,
            IOException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(getPrivate(keyFile));
        rsa.update(data.getBytes());
        return rsa.sign();
    }

    private static PrivateKey getPrivate(String filename)
            throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private static void writeToFile(String filename, byte[] signature) throws IOException {
        File f = new File(filename);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
        out.writeObject(signature);
        out.close();
        System.err.println("Signature created");
    }

    private static boolean verifySignature(byte[] data, byte[] signature, String keyFile)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException,
            SignatureException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(getPublic(keyFile));
        sig.update(data);

        return sig.verify(signature);
    }

    public static PublicKey getPublic(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static void downloadPackage(TomlParser.Package pkg)
            throws CLIException.PkgDowloadExcetion, CLIException.PkgSigDowloadExcetion {
        final String pkgURL = pkg.remote_repo + pkg.name + "-" + pkg.version + ".zip";
        final String pkgSigURL = pkg.remote_repo + pkg.name + "-" + pkg.version + ".zip.sig";

        try (BufferedInputStream in = new BufferedInputStream(new URL(pkgURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(pkg.path)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new CLIException.PkgDowloadExcetion(pkgURL);
        }
        if (pkg.verify_signature) {
            try (BufferedInputStream in = new BufferedInputStream(new URL(pkgSigURL).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(pkg.path + ".sig")) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new CLIException.PkgSigDowloadExcetion(pkgSigURL);
            }
        }
    }

    public static void buildPackage(Config config)
            throws CLIException.PkgNameException, CLIException.PkgCreatePkgExcetipn, CLIException.PkgAddToPkgException,
            IOException {
        final String pkgFileName = config.projectName + ".zip";
        if (config.projectName == null) {
            throw new CLIException.PkgNameException();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pkgFileName);
        } catch (FileNotFoundException e) {
            throw new CLIException.PkgCreatePkgExcetipn(pkgFileName);
        }
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (SourceFile srcFile : config.source) {
            File fileToZip = new File(srcFile.path);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(fileToZip);
            } catch (FileNotFoundException e) {
                throw new CLIException.PkgAddToPkgException(srcFile.path);
            }
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            try {
                zipOut.putNextEntry(zipEntry);
            } catch (IOException e) {
                throw new CLIException.PkgAddToPkgException(srcFile.path);
            }

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
    }
}

