package net.alagris;


import java.io.File;
import java.util.*;

public class SolomonoffBuildSystem {

    public static class BuildConfig {
        Mealy[] mealy;
        Kolmogorov[] kolmogorov;
        Infer[] infer;
    }

    public static class Infer {
        String path;
        String algorithm; //"RPNI", "OSTIA"
        String name;//name of produced transducer
    }

    public static class Mealy {
        String path;
    }

    public static class Kolmogorov {
        String path;
    }

    interface Minimize<G> {
        G minimize(G g) throws CompilationError;
    }

    public static <Pipeline, Var, V, E, P, A, O extends Seq<A>, W, N, G extends IntermediateGraph<V, E, P, N>> void
    run(File tomlConfig, ParseSpecs<Pipeline, Var, V, E, P, A, O, W, N, G> specs, Minimize<G> minimize) {
    }

}
