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
}
