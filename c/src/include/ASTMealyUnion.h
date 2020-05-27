#ifndef AST_MEALY_UNION_H
#define AST_MEALY_UNION_H

#include "ASTMealy.h"
#include <stdlib.h>

typedef struct ASTMealyUnion {
    ASTMealy * lmealy;
    ASTMealy * rmealy;
} ASTMealyUnion;

ASTMealy * createMealyUnion(ASTMealy * lMealy, ASTMealy rMealy);

#endif