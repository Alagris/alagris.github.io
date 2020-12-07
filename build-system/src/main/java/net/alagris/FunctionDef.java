package net.alagris;

import org.antlr.v4.runtime.Token;

public class FunctionDef {
    private Boolean exponential;
    private Token symbol;
    
    public FunctionDef(Boolean exponential, Token symbol) {
        this.exponential = exponential;
        this.symbol = symbol;
    }

    public Boolean isExponential() {
        return exponential;
    }

    public Token getSymbol() {
        return symbol;
    }
}
