package net.alagris;

import org.antlr.v4.runtime.Token;

public class CompileTask extends Thread {
    private Thread t;
    private Token token;
    
    CompileTask(Token token) {
        this.token = token;
    }

    public void run() {
        try {
            System.out.println("Running " +  token.getText() );
        } catch (CompilationError e) {
            System.out.println("Thread " +  token.getText() + " interrupted.");
        }
        System.out.println("Thread " +  token.getText() + " exiting.");
    }

    public void start () {
        if (t == null) {
            t = new Thread(this, token.getText());
            t.start ();
        }
    }
}
