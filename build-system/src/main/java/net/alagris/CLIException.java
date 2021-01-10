package net.alagris;

public class  extends Exception{
    private static final long serialVersionUID = 1L;

    public (String msg) {
        super(msg);
    }

    public (String msg, Throwable cause) {
        super(msg, cause);
    }

    public (Throwable cause) {
        super(cause);
    }

    public static class MealyFileException extends CLIException {
        private final Pos node;

        public ParseException(Pos node, Throwable cause) {
            super(cause);
            this.node = node;
        }

        public Pos getNode() {
            return node;
        }
    }

}
