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
}
