#ifndef AST_MEALY_CONCAT_H
#define AST_MEALY_CONCAT_H

#include "ASTMealy.h"
#include <stdlib.h>

typedef struct ASTMealyConcat {
    ASTMealy * lMealy;
    ASTMealy * rMealy;
} ASTMealyConcat;

ASTMealy * createMealyConcat(ASTMealy * lMealy, ASTMealy rMealy);

#endif