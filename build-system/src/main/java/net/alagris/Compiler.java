package net.alagris.build_system;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;

public class Compiler {

    public static void main(String[] args) {
        String source = "";
        final GrammarLexer lexer = new GrammarLexer(CharStreams.fromString(source));
        final GrammarParser parser = new GrammarParser(new CommonTokenStream(lexer));
        MyGrammarListener listener = new MyGrammarListener();
        listener.statements.push(new ArrayList<Statement>());
        System.out.println("Push initial blank statements "+listener.statements);
        ParseTreeWalker.DEFAULT.walk(listener, parser.start());
        ArrayList<Statement> output = listener.statements.pop();
        System.out.println(output);
    }
}
