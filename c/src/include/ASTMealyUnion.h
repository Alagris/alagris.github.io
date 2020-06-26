#ifndef AST_MEALY_UNION_H
#define AST_MEALY_UNION_H

#include "ASTMealy.h"
#include <stdlib.h>

ASTMealy * createMealyUnion(ASTMealy * lMealy, ASTMealy * rMealy);

#endif