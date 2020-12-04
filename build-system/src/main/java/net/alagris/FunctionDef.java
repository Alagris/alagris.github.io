package net.alagris;

public class FunctionDef {
    final private Boolean exponential;
    final private String name;

    public FunctionDef() {
        exponential = null;
    }

//    public FunctionDef(Boolean exponential, String name) {
//        this.exponential = exponential;
//        this.name = name;
//    }

    public Boolean isExponential() {
        return getExponential();
    }

    public String getName() {
        return name;
    }

    public Boolean getExponential() {
        return exponential;
    }
}
