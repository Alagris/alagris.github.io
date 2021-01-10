package net.alagris;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface SolomonoffWeighted {

    static final SolomonoffWeighted EPSILON = new Str(false,IntSeq.Epsilon,null);

    <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G 
    compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String,G> varAssignment) 
            throws CompilationError;

    public static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G 
    weightBefore(int weight, G nested,ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs) 
            throws CompilationError {
        return specs.specification()
                .leftActionOnGraph(specs.specification()
                        .partialWeightedEdge(specs.specification().parseW(weight)), nested);
    }

    public static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G
    weightAfter(G nested, int weight,ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs) throws CompilationError {
        return specs.specification()
                .rightActionOnGraph(nested, specs.specification()
                        .partialWeightedEdge(specs.specification().parseW(weight)));
    }

    int precedence();
    class Stringifier{
        final StringBuilder sb;
        final Map<String, Integer> usagesLeft;

        public Stringifier(StringBuilder sb, Map<String, Integer> usagesLeft) {
            this.sb = sb;
            this.usagesLeft = usagesLeft;
        }
    }
    void toString(Stringifier sb);

    class WeightAndAST {
        final int weight;
        final SolomonoffWeighted sol;

        public WeightAndAST(int weight, SolomonoffWeighted sol) {
            this.weight = weight;
            this.sol = sol;
        }
    }

    static class Union implements SolomonoffWeighted {
        final WeightAndAST[] union;

        public Union(WeightAndAST[] union) {
            this.union = union;
            assert union.length>0;
        }

        @Override
        public <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G
        compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String, G> varAssignment)
                throws CompilationError {
            G lhs = union[0].sol.compile(specs, varAssignment);
            if (union[0].weight != 0) {
                lhs = weightBefore(union[0].weight, lhs, specs);
            }
            for (int i = 1; i < union.length; i++) {
                G rhs = union[i].sol.compile(specs, varAssignment);
                if (union[i].weight != 0) {
                    rhs = weightBefore(union[i].weight, rhs, specs);
                }
                lhs = specs.specification().union(lhs, rhs, specs.specification()::epsilonUnion);
            }
            return lhs;
        }

        @Override
        public int precedence() {
            return 0;
        }

        @Override
        public void toString(Stringifier sb) {
            WeightAndAST e = union[0];
            if (e.weight != 0) {
                sb.sb.append(e.weight).append(" ");
            }
            if (e.sol.precedence() < precedence()) {
                sb.sb.append("(");
                e.sol.toString(sb);
                sb.sb.append(")");
            } else {
                e.sol.toString(sb);
            }
            for (int i = 1; i < union.length; i++) {
                sb.sb.append("|");
                e = union[i];
                if (e.weight != 0) {
                    sb.sb.append(e.weight).append(" ");
                }
                if (e.sol.precedence() < precedence()) {
                    sb.sb.append("(");
                    e.sol.toString(sb);
                    sb.sb.append(")");
                } else {
                    e.sol.toString(sb);
                }
            }
        }
    }

    static class Concat implements SolomonoffWeighted {
        final WeightAndAST[] concat;

        public Concat(WeightAndAST[] concat) {
            this.concat = concat;
            assert concat.length > 0;
        }

        @Override
        public <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G
        compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String, G> varAssignment)
                throws CompilationError{
            G lhs = concat[0].sol.compile(specs, varAssignment);
            if (concat[0].weight != 0){
                lhs = weightAfter(lhs, concat[0].weight, specs);
            }
            for (int i=1; i < concat.length; i++) {
                G rhs = concat[i].sol.compile(specs, varAssignment);
                if( concat[i].weight != 0) {
                    rhs = weightAfter(rhs,concat[i].weight, specs);
                }
                lhs = specs.specification().concat(lhs, rhs);
            }
            return lhs;
        }

        @Override
        public int precedence() {
            return 1;
        }

        @Override
        public void toString(Stringifier sb) {
            WeightAndAST e = concat[0];
            if (e.sol.precedence() < precedence()) {
                sb.sb.append("(");
                e.sol.toString(sb);
                sb.sb.append(")");
            } else {
                e.sol.toString(sb);
            }
            if (e.weight!=0) {
                sb.sb.append(" ").append(e.weight);
            }
            for (int i=1;i<concat.length;i++) {
                sb.sb.append(" ");
                e = concat[i];
                if (e.sol.precedence()<precedence()) {
                    sb.sb.append("(");
                    e.sol.toString(sb);
                    sb.sb.append(")");
                } else {
                    e.sol.toString(sb);
                }
                if (e.weight !=0 ) {
                    sb.sb.append(" ").append(e.weight);
                }
            }
        }
    }

    static class Kleene implements SolomonoffWeighted {
        final int weightUnderKleene;
        final SolomonoffWeighted lhs;
        final char type;

        public Kleene(int weightUnderKleene, SolomonoffWeighted lhs, char type) {
            this.weightUnderKleene = weightUnderKleene;
            this.lhs = lhs;
            this.type = type;
        }

        @Override
        public <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G 
        compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String, G> varAssignment) 
                throws CompilationError {
            final G lhs = this.lhs.compile(specs, varAssignment);
            switch (type){
                case '+':
                    return specs.specification().kleeneSemigroup(lhs,specs.specification()::epsilonKleene);
                case '?':
                    return specs.specification().kleeneOptional(lhs,specs.specification()::epsilonKleene);
                case '*':
                    return specs.specification().kleene(lhs,specs.specification()::epsilonKleene);
                default:
                    throw new IllegalArgumentException(type + " is not valid kleene closure");
            }
        }

        @Override
        public int precedence() {
            return 2;
        }

        @Override
        public void toString(Stringifier sb) {
            if (lhs.precedence() < precedence()) {
                sb.sb.append("(");
                lhs.toString(sb);
                sb.sb.append(")");
            } else {
                lhs.toString(sb);
            }
            if(weightUnderKleene != 0) {
                sb.sb.append(" ").append(weightUnderKleene);
            }
            sb.sb.append("*");
        }
    }

    public static class Func implements SolomonoffWeighted {
        final SolomonoffWeighted[] args;
        final String id;
        final Pos pos;

        public Func(SolomonoffWeighted[] args, String id, Pos pos) {
            this.args = args;
            this.id = id;
            this.pos = pos;
        }

        @Override
        public <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G
        compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String,G> varAssignment)
                throws CompilationError {
            final ArrayList<G> argsG = new ArrayList<>(args.length);
            for (SolomonoffWeighted arg : args) {
                argsG.add(arg.compile(specs, varAssignment));
            }
            return specs.externalOperation(pos, id, argsG);
        }

        @Override
        public int precedence() {
            return 3;
        }

        @Override
        public void toString(Stringifier sb) {
            sb.sb.append(id).append("[");
            if (args.length > 0) {
                args[0].toString(sb);
                for (int i=1; i < args.length; i++) {
                    sb.sb.append(", ");
                    args[i].toString(sb);
                }
            }
            sb.sb.append("]");
        }
    }

    public static class Informant implements SolomonoffWeighted {
        final String id;
        private final Pos pos;
        final List<Pair<IntSeq, IntSeq>> informant;

        public Informant(String id,Pos pos, List<Pair<IntSeq, IntSeq>> informant) {
            this.id = id;
            this.pos = pos;
            this.informant = informant;
        }
        @Override
        public int precedence() {
            return 3;
        }
        @Override
        public <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G
        compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String,G> varAssignment)
                throws CompilationError {
            return specs.externalFunction(pos,id,Specification.mapListLazy(informant, p->{
                try {
                    final O l = specs.specification().parseStr(p.l());
                    final O r = p.r() == null ? null : specs.specification().parseStr(p.r());
                    return Pair.of(l, r);
                } catch (CompilationError e){
                    throw new RuntimeException(e);
                }
            }));
        }
        @Override
        public void toString(Stringifier sb) {
            sb.sb.append(id).append("!!(");
            if (!informant.isEmpty()) {
                final Iterator<Pair<IntSeq, IntSeq>> i = informant.iterator();
                Pair<IntSeq, IntSeq> e = i.next();
                sb.sb.append(e.l().toStringLiteral()).append(":").append(e.r()==null?"#":e.r().toStringLiteral());
                while (i.hasNext()) {
                    sb.sb.append(", ");
                    e = i.next();
                    sb.sb.append(e.l().toStringLiteral()).append(":").append(e.r()==null?"#":e.r().toStringLiteral());
                }
            }
            sb.sb.append(")");
        }
    }

    static class Range implements SolomonoffWeighted {
        final int fromInclusive;
        final int toInclusive;
        final TerminalNode node;
        public Range(int fromInclusive, int toInclusive, TerminalNode node) {
            this.fromInclusive = fromInclusive;
            this.toInclusive = toInclusive;
            this.node = node;
        }

        public Range(Pair.IntPair pair, TerminalNode node) {
            this(pair.l, pair.r, node);
        }


        @Override
        public <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G
        compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String,G> varAssignment)
                throws CompilationError {
            final Pair<A, A> range = specs.specification().parseRangeInclusive(fromInclusive, toInclusive);
            return specs.specification().atomicRangeGraph(specs.specification().metaInfoGenerator(node), range);
        }

        @Override
        public int precedence() {
            return 3;
        }

        @Override
        public void toString(Stringifier sb) {
            IntSeq.appendCodepointRange(sb.sb, fromInclusive, toInclusive);
        }
    }

    static class Str implements SolomonoffWeighted {
        final IntSeq str;
        final boolean out;
        final TerminalNode node;
        public Str(boolean out, IntSeq str, TerminalNode node) {
            this.str = str;
            this.node = node;
            this.out = out;
        }

        @Override
        public <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G
        compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String,G> varAssignment)
                throws CompilationError {
            final O o = specs.specification().parseStr(str);
            if (out) {
                return specs.specification().atomicEpsilonGraph(specs.specification().partialOutputEdge(o));
            } else {
                final Iterator<A> string = o.iterator();
                if (string.hasNext()) {
                    final V meta = specs.specification().metaInfoGenerator(node);
                    G concatenated = specs.specification().atomicRangeGraph(meta,
                                    specs.specification().symbolAsRange(string.next()));
                    while (string.hasNext()) {
                        final G atom = specs.specification().atomicRangeGraph(meta,
                                specs.specification().symbolAsRange(string.next()));
                        concatenated = specs.specification().concat(concatenated, atom);
                    }
                    return concatenated;
                } else {
                    return specs.specification().atomicEpsilonGraph();
                }
            }

        }
        @Override
        public int precedence() {
            return 3;
        }

        @Override
        public void toString(Stringifier sb) {
            if (out) {
                sb.sb.append(":");
            }
            sb.sb.append(str.toStringLiteral());
        }
    }

    static class Var implements SolomonoffWeighted{
        final String id;

        public Var(String id) {
            this.id = id;
        }

        @Override
        public <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> G
        compile(ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Function<String,G> varAssignment)
                throws CompilationError {
            return varAssignment.apply(id);
        }

        @Override
        public int precedence() {
            return 3;
        }

        @Override
        public void toString(Stringifier sb) {
            final Integer usagesLeft = sb.usagesLeft.computeIfPresent(id, (k, v) -> v - 1);
            assert usagesLeft != null;
            assert usagesLeft >= 0;
            if (usagesLeft > 0) {
                sb.sb.append("!!");
            }
            sb.sb.append(id);
        }
    }
}
