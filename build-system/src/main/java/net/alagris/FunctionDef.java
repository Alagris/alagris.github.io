package net.alagris;

public class FunctionDef {
    final private Boolean exponential;
    final private String name;
    
    public FunctionDef(Boolean exponential, String name) {
        this.exponential = exponential;
        this.name = name;
    }

    public Boolean isExponential() {
        return exponential;
    }

    public String getName() {
        return name;
    }
}
