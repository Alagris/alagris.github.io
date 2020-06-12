#include "ASTMealyAtomic.h"

ASTMealy * createMealyAtomic (InputExpression * input, LiteralList * output) {
    ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
    node->type = MEALY_ATOMIC;
    node->mealyAtomic = (ASTMealyAtomic) {input, output};
    return node;
}