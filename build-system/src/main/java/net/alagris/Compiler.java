package net.alagris;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.jgrapht.graph.*;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class Compiler {

    public static void main(String[] args) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get("sample.mealy"), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String source = contentBuilder.toString();
        final GrammarLexer lexer = new GrammarLexer(CharStreams.fromString(source));
        final GrammarParser parser = new GrammarParser(new CommonTokenStream(lexer));
        DirectedAcyclicGraph<Token, DefaultEdge> dag = new DirectedAcyclicGraph<>(DefaultEdge.class);
        final LinkedList<CompilationError> errors = new LinkedList<>();
        ParserListener listener = new ParserListener(dag, errors);
        ParseTreeWalker.DEFAULT.walk(listener, parser.start());
        
        Stack<LinkedList<Token>> stages = new Stack<>();

        while (true) {
            final Iterator<Token> topoIter = dag.iterator();
            if (!topoIter.hasNext()) {
                break;
            }
            final LinkedList<Token> stage = new LinkedList<>();
            for ( ; topoIter.hasNext(); ) {
                Token token = topoIter.next();
                if (dag.getDescendants(token).isEmpty()) {
                    stage.add(token);
                }
            }
            for (Token token : stage) {
                dag.removeVertex(token);
            }

            stages.push(stage);
        }
    }

}
