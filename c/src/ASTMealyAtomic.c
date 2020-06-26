#include "ASTMealyAtomic.h"

ASTMealy * createMealyAtomic (AST_FSA * input, StringList * output) {
    ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
    node->type = MEALY_ATOMIC;
    node->mealy.mealyAtomic.input = input;
    node->mealy.mealyAtomic.output = output;
    // node->mealyAtomic = (ASTMealyAtomic) {input, output};
    return node;
}