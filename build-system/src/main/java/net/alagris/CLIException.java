package net.alagris;

public class CLIException extends Exception{
    private static final long serialVersionUID = 1L;

    public CLIException(String msg) {
        super(msg);
    }

    public CLIException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CLIException(Throwable cause) {
        super(cause);
    }

    public static class MealyFileException extends CLIException {

        public MealyFileException(String fileName, Throwable cause) {
            super("Cannot find " + fileName, cause);
        }

        public MealyFileException(String fileName) {
            super("Cannot find " + fileName);
        }
    }

    public static class InputFileException extends CLIException {

        public InputFileException(String fileName, Throwable cause) {
            super("Cannot find " + fileName, cause);
        }
    }

    public static class BuildFileException extends CLIException {

        public BuildFileException(String fileName) {
            super("Cannot find " + fileName);
        }
    }

    public static class BinFileException extends CLIException {

        public BinFileException(String id) {
            super("Cannot find compiled file for " + id);
        }
    }

    public static class PKFileException extends CLIException {

        public PKFileException(String pkg) {
            super("Cannot find public key file for " + pkg);
        }
    }

    public static class PkgVerifyException extends CLIException {

        public PkgVerifyException(String pkg) {
            super("Cannot verify " + pkg);
        }
    }

    public static class InvalidSignatureException extends CLIException {

        public InvalidSignatureException(String pkg) {
            super("Bad signature " + pkg);
        }
    }

    public static class PkgDowloadExcetion extends CLIException {

        public PkgDowloadExcetion(String url) {
            super("Cannot download package " + url);
        }
    }
    
    public static class PkgSigDowloadExcetion extends CLIException {

        public PkgSigDowloadExcetion(String url) {
            super("Cannot download package signature " + url);
        }
    }

    public static class PkgNameException extends CLIException {

        public PkgNameException() {
            super("Invalid project name");
        }
    }

    public static class PkgAddToPkgException extends CLIException {
        public PkgAddToPkgException(String file) {
            super("Cannot add " + file + "to package");
        }
    }

    public static class PkgCreatePkgExcetipn extends CLIException {
        public PkgCreatePkgExcetipn(String file) {
            super("Cannot create " + file);
        }
    }

    public static class PkgSigningExcetipn extends CLIException {
        public PkgSigningExcetipn(String file) {
            super("Cannot sign " + file);
        }
    }
}
