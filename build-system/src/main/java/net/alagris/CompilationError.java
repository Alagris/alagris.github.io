package net.alagris;

public class CompilationError
{
    private final String message;

    public CompilationError(String message)
    {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}