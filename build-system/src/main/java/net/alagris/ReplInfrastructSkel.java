package net.alagris;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.util.*;

public class ReplInfrastructSkel {
    private final static Random RAND = new Random();

    public static class OptimisedHashLexTransducer
            extends OptimisedLexTransducer<HashMapIntermediateGraph.N<Pos, E>, HashMapIntermediateGraph<Pos, E, P>> {

        /**
         * @param eagerMinimisation This will cause automata to be minimized as soon as
         *                          they are parsed/registered (that is, the
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#pseudoMinimize}
         *                          will be automatically called from
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#introduceVariable})
         */
        public OptimisedHashLexTransducer(boolean eagerMinimisation, int minimalSymbol, int maximalSymbol,
                                          ExternalPipelineFunction externalPipelineFunction) throws CompilationError {
            super(new HashMapIntermediateGraph.LexUnicodeSpecification(eagerMinimisation, minimalSymbol, maximalSymbol,
                    externalPipelineFunction));
        }

        /**
         * @param eagerMinimisation This will cause automata to be minimized as soon as
         *                          they are parsed/registered (that is, the
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#pseudoMinimize}
         *                          will be automatically called from
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#introduceVariable})
         */
        public OptimisedHashLexTransducer(CharStream source, int minimalSymbol, int maximalSymbol,
                                          boolean eagerMinimisation, ExternalPipelineFunction externalPipelineFunction) throws CompilationError {
            this(eagerMinimisation, minimalSymbol, maximalSymbol, externalPipelineFunction);
            parse(source);
            checkStrongFunctionality();
        }

        /**
         * @param eagerMinimisation This will cause automata to be minimized as soon as
         *                          they are parsed/registered (that is, the
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#pseudoMinimize}
         *                          will be automatically called from
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#introduceVariable})
         */
        public OptimisedHashLexTransducer(String source, int minimalSymbol, int maximalSymbol,
                                          boolean eagerMinimisation, ExternalPipelineFunction externalPipelineFunction) throws CompilationError {
            this(CharStreams.fromString(source), minimalSymbol, maximalSymbol, eagerMinimisation,
                    externalPipelineFunction);
        }

        public OptimisedHashLexTransducer(int minimalSymbol, int maximalSymbol) throws CompilationError {
            this(minimalSymbol, maximalSymbol, true);
        }

        public OptimisedHashLexTransducer(int minimalSymbol, int maximalSymbol, boolean eagerMinimisation)
                throws CompilationError {
            this(eagerMinimisation, minimalSymbol, maximalSymbol, makeEmptyExternalPipelineFunction());
        }

        /**
         * @param eagerMinimisation This will cause automata to be minimized as soon as
         *                          they are parsed/registered (that is, the
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#pseudoMinimize}
         *                          will be automatically called from
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#introduceVariable})
         */
        public OptimisedHashLexTransducer(CharStream source, int minimalSymbol, int maximalSymbol,
                                          boolean eagerMinimisation) throws CompilationError {
            this(source, minimalSymbol, maximalSymbol, eagerMinimisation, makeEmptyExternalPipelineFunction());
        }

        /**
         * @param eagerMinimisation This will cause automata to be minimized as soon as
         *                          they are parsed/registered (that is, the
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#pseudoMinimize}
         *                          will be automatically called from
         *                          {@link HashMapIntermediateGraph.LexUnicodeSpecification#introduceVariable})
         */
        public OptimisedHashLexTransducer(String source, int minimalSymbol, int maximalSymbol,
                                          boolean eagerMinimisation) throws CompilationError {
            this(source, minimalSymbol, maximalSymbol, eagerMinimisation, makeEmptyExternalPipelineFunction());
        }
    }
}
