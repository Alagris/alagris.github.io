#include "ASTMealyAtomic.h"

ASTMealy * createMealyAtomic (AST_FSA * fsa, LiteralList * output) {
    ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
    node->type = MEALY_ATOMIC;
    node->mealyAtomic = {fsa, output};
    return node;
}