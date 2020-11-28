package net.alagris;

import net.alagris.GrammarParser.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.HashSet;
import java.util.Optional;

public class ParserListener implements GrammarListener {

    final private HashSet<FunctionDef> functions;
    
    public ParserListener(HashSet functions) {
        this.functions = functions;
    }
    
    @Override
    public void enterStart(StartContext ctx) {

    }

    @Override
    public void exitStart(StartContext ctx) {

    }

    @Override
    public void enterEndFuncs(EndFuncsContext ctx) {

    }

    @Override
    public void exitEndFuncs(EndFuncsContext ctx) {

    }

    @Override
    public void enterFuncDef(FuncDefContext ctx) {
    }

    @Override
    public void exitFuncDef(FuncDefContext ctx) {
        final Boolean exponential = (ctx.exponential != null) ? true : false;
        final String name = ctx.ID().getText();
        functions.add(new FunctionDef(exponential, name));
    }

    @Override
    public void enterHoarePipeline(HoarePipelineContext ctx) {

    }

    @Override
    public void exitHoarePipeline(HoarePipelineContext ctx) {

    }

    @Override
    public void enterTypeJudgement(TypeJudgementContext ctx) {

    }

    @Override
    public void exitTypeJudgement(TypeJudgementContext ctx) {

    }

    @Override
    public void enterPipelineMealy(PipelineMealyContext ctx) {

    }

    @Override
    public void exitPipelineMealy(PipelineMealyContext ctx) {

    }

    @Override
    public void enterPipelineExternal(PipelineExternalContext ctx) {

    }

    @Override
    public void exitPipelineExternal(PipelineExternalContext ctx) {

    }

    @Override
    public void enterPipelineNested(PipelineNestedContext ctx) {

    }

    @Override
    public void exitPipelineNested(PipelineNestedContext ctx) {

    }

    @Override
    public void enterPipelineBegin(PipelineBeginContext ctx) {

    }

    @Override
    public void exitPipelineBegin(PipelineBeginContext ctx) {

    }

    @Override
    public void enterMealyUnion(MealyUnionContext ctx) {

    }

    @Override
    public void exitMealyUnion(MealyUnionContext ctx) {

    }

    @Override
    public void enterMealyEndConcat(MealyEndConcatContext ctx) {

    }

    @Override
    public void exitMealyEndConcat(MealyEndConcatContext ctx) {

    }

    @Override
    public void enterMealyMoreConcat(MealyMoreConcatContext ctx) {

    }

    @Override
    public void exitMealyMoreConcat(MealyMoreConcatContext ctx) {

    }

    @Override
    public void enterMealyKleeneClosure(MealyKleeneClosureContext ctx) {

    }

    @Override
    public void exitMealyKleeneClosure(MealyKleeneClosureContext ctx) {

    }

    @Override
    public void enterMealyNoKleeneClosure(MealyNoKleeneClosureContext ctx) {

    }

    @Override
    public void exitMealyNoKleeneClosure(MealyNoKleeneClosureContext ctx) {

    }

    @Override
    public void enterMealyProduct(MealyProductContext ctx) {

    }

    @Override
    public void exitMealyProduct(MealyProductContext ctx) {

    }

    @Override
    public void enterMealyProductCodepoints(MealyProductCodepointsContext ctx) {

    }

    @Override
    public void exitMealyProductCodepoints(MealyProductCodepointsContext ctx) {

    }

    @Override
    public void enterMealyEpsilonProduct(MealyEpsilonProductContext ctx) {

    }

    @Override
    public void exitMealyEpsilonProduct(MealyEpsilonProductContext ctx) {

    }

    @Override
    public void enterMealyAtomicLiteral(MealyAtomicLiteralContext ctx) {

    }

    @Override
    public void exitMealyAtomicLiteral(MealyAtomicLiteralContext ctx) {

    }

    @Override
    public void enterMealyAtomicRange(MealyAtomicRangeContext ctx) {

    }

    @Override
    public void exitMealyAtomicRange(MealyAtomicRangeContext ctx) {

    }

    @Override
    public void enterMealyAtomicCodepoint(MealyAtomicCodepointContext ctx) {

    }

    @Override
    public void exitMealyAtomicCodepoint(MealyAtomicCodepointContext ctx) {

    }

    @Override
    public void enterMealyAtomicVarID(MealyAtomicVarIDContext ctx) {

    }

    @Override
    public void exitMealyAtomicVarID(MealyAtomicVarIDContext ctx) {
        String test = ctx.getText();
        final Optional<FunctionDef> functionDef =
                functions.stream().filter(x -> x.getName().equals(ctx.ID().getText())).findFirst();
        functionDef.ifPresent(x -> {
            if(!x.isExponential()) {
                functions.remove(x);
            }
        });

//        functionDef.orElseThrow()
    }

    @Override
    public void enterMealyAtomicExternal(MealyAtomicExternalContext ctx) {

    }

    @Override
    public void exitMealyAtomicExternal(MealyAtomicExternalContext ctx) {

    }

    @Override
    public void enterMealyAtomicNested(MealyAtomicNestedContext ctx) {

    }

    @Override
    public void exitMealyAtomicNested(MealyAtomicNestedContext ctx) {

    }

    @Override
    public void enterMealyAtomicExternalOperation(MealyAtomicExternalOperationContext ctx) {

    }

    @Override
    public void exitMealyAtomicExternalOperation(MealyAtomicExternalOperationContext ctx) {

    }

    @Override
    public void enterInformant(InformantContext ctx) {

    }

    @Override
    public void exitInformant(InformantContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode node) {

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }
}

