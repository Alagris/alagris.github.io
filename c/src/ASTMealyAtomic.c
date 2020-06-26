#include "ASTMealyAtomic.h"

ASTMealy * createMealyAtomic (AST_FSA * input, StringList * output) {
    ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
    node->type = MEALY_ATOMIC;
    node->mealy.mealyAtomic.input = input;
    node->mealy.mealyAtomic.output = output;
    // node->mealyAtomic = (ASTMealyAtomic) {input, output};
    return node;
}

AST_FSA * createMealyAtomicPhantom (AST_FSA * input, char * output) {
    AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
    node->type = MEALY_PHANTOM;
    node->fsa.mealyPhantom.in = input;
    node->fsa.mealyPhantom.out = output;
    return node;
}