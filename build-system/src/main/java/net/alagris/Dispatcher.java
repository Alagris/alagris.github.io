package net.alagris;

import org.antlr.v4.runtime.Token;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class Dispatcher {

    public static Stack<LinkedList<Token>> sequence(DirectedAcyclicGraph<Token, DefaultEdge> dag) {
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
        return stages;
    }
    
    public static void compile(Stack<LinkedList<Token>> stages) throws InterruptedException {
        LinkedList<CompileTask> tasks = new LinkedList<>();
        for (LinkedList<Token> stage : stages) {
            for (Token token : stage) {
                CompileTask task = new CompileTask(token);
                tasks.add(task);
                task.run();
            }
            for (CompileTask task : tasks) {
                task.join();
            }

        }

    }
}
