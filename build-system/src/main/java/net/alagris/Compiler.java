package net.alagris;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Spliterator;
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

        final LinkedList<HashSet<Token>> stages = new LinkedList<>();
        TopologicalOrderIterator<Token, DefaultEdge> dagIter = new TopologicalOrderIterator<>(dag);
        HashSet<Token> currentStage = new HashSet<>();
        while (dagIter.hasNext()) {
            final Token token = dagIter.next();
            for (Token tokensInCurrentStage : currentStage) {
                Set<Token> ancestors = dag.getAncestors(tokensInCurrentStage);
                if (ancestors.contains(token)) {
                    stages.add(currentStage);
                    currentStage = new HashSet<>();
                    break;
                }
            }
            currentStage.add(token);
        }
        for (HashSet<Token> a : stages) {
            System.out.println(a);
        }
    }

    public static String call(Spliterator<Token> spliterator) {
        AtomicInteger current = new AtomicInteger();
        String a_name;
        while (spliterator.tryAdvance(a -> {
            System.out.println(a.getText());
            current.getAndIncrement();
        }));
        return Thread.currentThread().getName() + ":" + current;
    }
}
