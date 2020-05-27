#ifndef AST_MEALY_ATOMIC_H
#define AST_MEALY_ATOMIC_H

#include "AST_FSA.h"
#include "ASTMealy.h"
#include "LiteralList.h"
#include <stdlib.h>

typedef struct ASTMealyAtomic {
    AST_FSA * fsa;
    LiteralList * output;
} ASTMealyAtomic;

ASTMealy * createMealyAtomic (AST_FSA * fsa, LiteralList * output);

#endif