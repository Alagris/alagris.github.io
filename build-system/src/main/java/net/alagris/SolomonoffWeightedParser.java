package net.alagris;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SolomonoffWeightedParser implements SolomonoffGrammarListener {

    final Stack<SolomonoffWeighted> stack = new Stack<>();
    private final ResultCollector collector;
    private final String sourceFile;
    String currentVariable = null;

    interface ResultCollector{
        void define(String var, SolomonoffWeighted definition, String sourceFile);
        void dependsOn(String varX,String varY);
        /**@param isFunction true for function types, false for product types*/
        void typeOf(String varX, SolomonoffWeighted in, SolomonoffWeighted out, boolean isFunction);
    }
    public static class ConcurrentCollector implements  ResultCollector {
        final Set<Pair<String,String>> dependsOn = ConcurrentHashMap.newKeySet();
        final ConcurrentHashMap<String, SolomonoffWeighted> definitions = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, String> sourceFiles = new ConcurrentHashMap<>();
        public static class Type {
            final String var;
            final SolomonoffWeighted in;
            final SolomonoffWeighted out;
            final boolean isFunction;

            Type(String var, SolomonoffWeighted in, SolomonoffWeighted out, boolean isFunction) {
                this.var = var;
                this.in = in;
                this.out = out;
                this.isFunction = isFunction;
            }
        }
        final ConcurrentLinkedQueue<Type> types = new ConcurrentLinkedQueue<>();

        @Override
        public void define(String var, SolomonoffWeighted definition, String sourceFile) {
            sourceFiles.put(var, sourceFile);
            final SolomonoffWeighted prev = definitions.put(var, definition);
            if (prev!=null) {
                throw new IllegalArgumentException("Variable " + var + " has been defined twice!");
            }
        }

        @Override
        public void dependsOn(String varX, String varY) {
            dependsOn.add(Pair.of(varX,varY));
        }

        @Override
        public void typeOf(String varX, SolomonoffWeighted in, SolomonoffWeighted out, boolean isFunction) {
            types.add(new Type(varX, in, out, isFunction));
        }

    }

    public SolomonoffWeightedParser(ResultCollector collector, String sourceFile){
        this.collector = collector;
        this.sourceFile = sourceFile;
    }

    @Override
    public void enterStart(SolomonoffGrammarParser.StartContext startContext) {}

    @Override
    public void exitStart(SolomonoffGrammarParser.StartContext startContext) {}

    @Override
    public void enterRepl(SolomonoffGrammarParser.ReplContext replContext) {}

    @Override
    public void exitRepl(SolomonoffGrammarParser.ReplContext replContext) {}

    @Override
    public void enterFuncs(SolomonoffGrammarParser.FuncsContext funcsContext) {}

    @Override
    public void exitFuncs(SolomonoffGrammarParser.FuncsContext funcsContext) {}

    @Override
    public void enterFuncDef(SolomonoffGrammarParser.FuncDefContext funcDefContext) {
        currentVariable = funcDefContext.ID().getText();
    }

    @Override
    public void exitFuncDef(SolomonoffGrammarParser.FuncDefContext funcDefContext) {
        final SolomonoffWeighted def = stack.pop();
        assert currentVariable.equals(funcDefContext.ID().getText());
        collector.define(currentVariable, def, sourceFile);
        assert stack.isEmpty();
        currentVariable = null;
    }

    @Override
    public void enterTypeJudgement(SolomonoffGrammarParser.TypeJudgementContext typeJudgementContext) {
        assert currentVariable == null;
    }

    @Override
    public void exitTypeJudgement(SolomonoffGrammarParser.TypeJudgementContext ctx) {
        assert currentVariable == null;
        final String id = ctx.ID().getText();
        if (ctx.type == null) {
            final SolomonoffWeighted in = stack.pop();
            assert stack.isEmpty();
            collector.typeOf(id,in, SolomonoffWeighted.EPSILON,false);
        } else {
            final SolomonoffWeighted out = stack.pop();
            final SolomonoffWeighted in = stack.pop();
            assert stack.isEmpty();
            switch (ctx.type.getText()) {
                case "&&":
                case "⨯":
                    collector.typeOf(id,in, out,false);
                    break;
                case "→":
                case "->":
                    collector.typeOf(id,in, out,true);
                    break;
            }
        }
        assert currentVariable == null;
    }

    @Override
    public void enterHoarePipeline(SolomonoffGrammarParser.HoarePipelineContext hoarePipelineContext) {}

    @Override
    public void exitHoarePipeline(SolomonoffGrammarParser.HoarePipelineContext hoarePipelineContext) {}

    @Override
    public void enterIncludeFile(SolomonoffGrammarParser.IncludeFileContext includeFileContext) {}

    @Override
    public void exitIncludeFile(SolomonoffGrammarParser.IncludeFileContext includeFileContext) {}

    @Override
    public void enterWaitForFile(SolomonoffGrammarParser.WaitForFileContext waitForFileContext) {}

    @Override
    public void exitWaitForFile(SolomonoffGrammarParser.WaitForFileContext waitForFileContext) {}

    @Override
    public void enterPipelineMealy(SolomonoffGrammarParser.PipelineMealyContext pipelineMealyContext) {}

    @Override
    public void exitPipelineMealy(SolomonoffGrammarParser.PipelineMealyContext pipelineMealyContext) {}

    @Override
    public void enterPipelineExternal(SolomonoffGrammarParser.PipelineExternalContext pipelineExternalContext) {}

    @Override
    public void exitPipelineExternal(SolomonoffGrammarParser.PipelineExternalContext pipelineExternalContext) {}

    @Override
    public void enterPipelineNested(SolomonoffGrammarParser.PipelineNestedContext pipelineNestedContext) {}

    @Override
    public void exitPipelineNested(SolomonoffGrammarParser.PipelineNestedContext pipelineNestedContext) {}

    @Override
    public void enterPipelineBegin(SolomonoffGrammarParser.PipelineBeginContext pipelineBeginContext) {}

    @Override
    public void exitPipelineBegin(SolomonoffGrammarParser.PipelineBeginContext pipelineBeginContext) {}

    @Override
    public void enterMealyUnion(SolomonoffGrammarParser.MealyUnionContext mealyUnionContext) {}

    @Override
    public void exitMealyUnion(SolomonoffGrammarParser.MealyUnionContext ctx) {
        final int elements = ctx.mealy_concat().size();
        final int children = ctx.children.size();
        assert elements > 0;
        int i = 0;
        int childIdx = 0;
        final SolomonoffWeighted.WeightAndAST[] union = new SolomonoffWeighted.WeightAndAST[elements];
        while (childIdx < children) {
            final int stackIdx = stack.size() - elements + i;
            assert stackIdx < stack.size();
            final SolomonoffWeighted ast = stack.get(stackIdx);
            final int weight;
            final ParseTree concatOrWeight = ctx.children.get(childIdx);
            if (concatOrWeight instanceof TerminalNode) {
                final TerminalNode weightNode = (TerminalNode) concatOrWeight;
                assert weightNode.getSymbol().getType() == SolomonoffGrammarLexer.Weight;
                weight = Integer.parseInt(weightNode.getText());
                childIdx += 1;
            } else {
                weight = 0;
            }
            assert ctx.children.get(childIdx) instanceof SolomonoffGrammarParser.MealyConcatContext;
            assert childIdx + 1 == children || ctx.children.get(childIdx + 1).getText().equals("|");
            union[i] = new SolomonoffWeighted.WeightAndAST(weight, ast);
            childIdx += 2;
            i++;
        }
        assert i == union.length;
        stack.setSize(stack.size() - elements);
        stack.push(new SolomonoffWeighted.Union(union));
    }

    @Override
    public void enterMealyConcat(SolomonoffGrammarParser.MealyConcatContext mealyConcatContext) {}

    @Override
    public void exitMealyConcat(SolomonoffGrammarParser.MealyConcatContext ctx) {
        final int elements = ctx.mealy_Kleene_closure().size();
        final int children = ctx.children.size();
        assert elements > 0;
        int i = 0;
        int childIdx = 0;
        final SolomonoffWeighted.WeightAndAST[] concat = new SolomonoffWeighted.WeightAndAST[elements];
        while (childIdx < children) {
            final int stackIdx = stack.size() - elements + i;
            assert stackIdx < stack.size();
            final SolomonoffWeighted ast = stack.get(stackIdx);
            assert ctx.children.get(childIdx)
                    instanceof SolomonoffGrammarParser.MealyKleeneClosureContext:ast.getClass() + " " + i + " "
                    + Specification.mapListLazy(ctx.children, ParseTree::getClass);
            childIdx++;
            final int weight;
            if(childIdx<children) {
                final ParseTree dotOrWeightOrKleene = ctx.children.get(childIdx);
                if (dotOrWeightOrKleene instanceof TerminalNode) {
                    final TerminalNode dotOrWeight = (TerminalNode) dotOrWeightOrKleene;
                    if (dotOrWeight.getSymbol().getType() == SolomonoffGrammarLexer.Weight) {
                        weight = Integer.parseInt(dotOrWeight.getText());
                        if (childIdx + 1 < children && ctx.children.get(childIdx + 1) instanceof TerminalNode) {
                            childIdx += 2;
                            assert ctx.children.get(childIdx + 1).getText().equals("∙");
                        } else {
                            childIdx += 1;
                        }
                    } else {
                        weight = 0;
                        assert dotOrWeight.getText().equals("∙");
                        childIdx += 1;
                    }
                } else {
                    weight = 0;
                }
            }else{
                weight = 0;
            }
            concat[i] = new SolomonoffWeighted.WeightAndAST(weight, ast);
            i++;
        }
        assert i == concat.length;
        stack.setSize(stack.size() - elements);
        stack.push(new SolomonoffWeighted.Concat(concat));
    }

    @Override
    public void enterMealyKleeneClosure(SolomonoffGrammarParser.MealyKleeneClosureContext mealyKleeneClosureContext) {}

    @Override
    public void exitMealyKleeneClosure(SolomonoffGrammarParser.MealyKleeneClosureContext ctx) {
        final String type;
        if (ctx.optional != null) {
            type = ctx.optional.getText();
        } else if (ctx.plus != null) {
            type = ctx.plus.getText();
        } else if (ctx.star != null) {
            type = ctx.star.getText();
        } else {
            assert ctx.Weight() == null;
            return;//pass
        }
        assert type.length() == 1;
        int weight = ctx.Weight() == null ? 0 : Integer.parseInt(ctx.Weight().getText());
        SolomonoffWeighted nested = stack.pop();
        nested = new SolomonoffWeighted.Kleene(weight, nested, type.charAt(0));
        stack.push(nested);
    }

    @Override
    public void enterMealyAtomicLiteral(SolomonoffGrammarParser.MealyAtomicLiteralContext mealyAtomicLiteralContext) {}

    @Override
    public void exitMealyAtomicLiteral(SolomonoffGrammarParser.MealyAtomicLiteralContext ctx) {
        stack.push(new SolomonoffWeighted.Str(
        ctx.colon != null, ParserListener.parseQuotedLiteral(ctx.StringLiteral()),ctx.StringLiteral()));
    }

    @Override
    public void enterMealyAtomicRange(SolomonoffGrammarParser.MealyAtomicRangeContext mealyAtomicRangeContext) {}

    @Override
    public void exitMealyAtomicRange(SolomonoffGrammarParser.MealyAtomicRangeContext ctx) {
        stack.push(new SolomonoffWeighted.Range(ParserListener.parseRange(ctx.Range()), ctx.Range()));
    }

    @Override
    public void enterMealyAtomicCodepointRange(
            SolomonoffGrammarParser.MealyAtomicCodepointRangeContext mealyAtomicCodepointRangeContext) {}

    @Override
    public void exitMealyAtomicCodepointRange(SolomonoffGrammarParser.MealyAtomicCodepointRangeContext ctx) {
        stack.push(new SolomonoffWeighted.Range(
                ParserListener.parseCodepointRange(ctx.CodepointRange()), ctx.CodepointRange()));
    }

    @Override
    public void enterMealyAtomicCodepoint(
            SolomonoffGrammarParser.MealyAtomicCodepointContext mealyAtomicCodepointContext) {}

    @Override
    public void exitMealyAtomicCodepoint(SolomonoffGrammarParser.MealyAtomicCodepointContext ctx) {
        stack.push(new SolomonoffWeighted.Str(
                ctx.colon!=null,ParserListener.parseCodepoint(ctx.Codepoint()),ctx.Codepoint()));
    }

    @Override
    public void enterMealyAtomicVarID(SolomonoffGrammarParser.MealyAtomicVarIDContext mealyAtomicVarIDContext) {}

    @Override
    public void exitMealyAtomicVarID(SolomonoffGrammarParser.MealyAtomicVarIDContext ctx) {
        final String id = ctx.ID().getText();
        if(currentVariable!=null){
            collector.dependsOn(currentVariable,id);
        }
        stack.push(new SolomonoffWeighted.Var(id));
    }

    @Override
    public void enterMealyAtomicExternal(
            SolomonoffGrammarParser.MealyAtomicExternalContext mealyAtomicExternalContext) {}

    @Override
    public void exitMealyAtomicExternal(SolomonoffGrammarParser.MealyAtomicExternalContext ctx) {
        stack.push(new SolomonoffWeighted.Informant(ctx.ID().getText(),
                new Pos(ctx.ID().getSymbol()),
                ParserListener.parseInformant(ctx.informant(),i->i,IntSeq.Epsilon,null)));
    }

    @Override
    public void enterMealyAtomicNested(SolomonoffGrammarParser.MealyAtomicNestedContext mealyAtomicNestedContext) {}

    @Override
    public void exitMealyAtomicNested(SolomonoffGrammarParser.MealyAtomicNestedContext mealyAtomicNestedContext) {}

    @Override
    public void enterMealyAtomicExternalOperation(SolomonoffGrammarParser.MealyAtomicExternalOperationContext ctx) {}

    @Override
    public void exitMealyAtomicExternalOperation(SolomonoffGrammarParser.MealyAtomicExternalOperationContext ctx) {
        final int unions = ctx.mealy_union().size();
        final SolomonoffWeighted[] unionArray = new SolomonoffWeighted[unions];
        for (int i = 0; i < unions; i++) {
            unionArray[i] = stack.get(stack.size() - unions + i);
        }
        stack.setSize(stack.size() - unions);
        stack.push(new SolomonoffWeighted.Func(unionArray,ctx.ID().getText(), new Pos(ctx.ID().getSymbol())));
    }

    @Override
    public void enterInformant(SolomonoffGrammarParser.InformantContext informantContext) {}

    @Override
    public void exitInformant(SolomonoffGrammarParser.InformantContext informantContext) {}

    @Override
    public void visitTerminal(TerminalNode terminalNode) {}

    @Override
    public void visitErrorNode(ErrorNode errorNode) {}

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {}

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {}
}
