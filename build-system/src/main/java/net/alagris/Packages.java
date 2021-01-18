package net.alagris;

import net.alagris.TomlParser.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Packages {
    public static boolean verifyPackage(TomlParser.Package pkg)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException,
            SignatureException, CLIException.PKFileException {
        if (pkg.verify_signature == true) {
            if (pkg.public_key == null) {
                throw new CLIException.PKFileException(pkg.name);
            }
            return Packages._verifyPackage(pkg.path, pkg.public_key);
        }
        return true;
    }

    private static boolean _verifyPackage(String pkg, String pk)
            throws InvalidKeySpecException, SignatureException, NoSuchAlgorithmException, InvalidKeyException,
            IOException {
        byte[] pkgData = Files.readAllBytes(Paths.get(pkg));
        Path sigPath = Paths.get(pkg.toString() + ".sig");
        byte[] sigData = Files.readAllBytes(sigPath);
        return verifySignature(pkgData, sigData, pk);
    }

    private static void sign(String pkg, String pkgSigFile, String keyFile)
            throws InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException,
            IOException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(getPrivate(keyFile));
        rsa.update(Files.readAllBytes(Paths.get(pkg)));
        writeToFile(pkgSigFile, rsa.sign());
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
        FileOutputStream out = new FileOutputStream(filename);
        out.write(signature);
        out.close();
        System.err.println("Signature created");
    }

    private static boolean verifySignature(byte[] data, byte[] signature, String keyFile)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException,
            SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
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
            throws CLIException.PkgDowloadExcetion, CLIException.PkgSigDownloadExcetion, UnsupportedEncodingException,
            MalformedURLException {
        final String pkgURL = URLEncoder.encode(pkg.name + "-" + pkg.version + ".zip",
                StandardCharsets.UTF_8.toString());
        final String pkgSigURL = URLEncoder.encode(pkg.name + "-" + pkg.version + ".zip.sig",
                StandardCharsets.UTF_8.toString());

        URL url = new URL(new URL(pkg.remote_repo), pkgURL);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(pkg.path)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new CLIException.PkgDowloadExcetion(url.toString());
        }
        System.err.println("Package " + pkg.name + " " + pkg.version + " downloaded");
        if (pkg.verify_signature) {
            URL sigURL = new URL(new URL(pkg.remote_repo), pkgSigURL);
            try (BufferedInputStream in =
                         new BufferedInputStream(sigURL.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(pkg.path + ".sig")) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new CLIException.PkgSigDownloadExcetion(sigURL.toString());
            }
            System.err.println("Signature for " + pkg.name + " " + pkg.version + " downloaded");
        }
    }

    public static void buildPackage(Config config)
            throws CLIException.PkgNameException, CLIException.PkgCreatePkgExcetipn, CLIException.PkgAddToPkgException,
            IOException, CLIException.PkgSigningExcetipn, CLIException.PkgVersionException {
        final String pkgFileName = config.project_name;
        final String pkgSigFileName = pkgFileName + ".sig";
        if (config.project_name == null) {
            throw new CLIException.PkgNameException();
        }
        if (config.version == null) {
            throw new CLIException.PkgVersionException();
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
        System.err.println("Package " + pkgFileName + " created");
        try {
            if (config.sign_pkg) {
                sign(pkgFileName, pkgSigFileName, config.private_key);
            }
        } catch (InvalidKeyException e) {
            throw new CLIException.PkgSigningExcetipn(pkgFileName);
        } catch (SignatureException e) {
            throw new CLIException.PkgSigningExcetipn(pkgFileName);
        } catch (InvalidKeySpecException e) {
            throw new CLIException.PkgSigningExcetipn(pkgFileName);
        } catch (NoSuchAlgorithmException e) {
            throw new CLIException.PkgSigningExcetipn(pkgFileName);
        }

    }
}

