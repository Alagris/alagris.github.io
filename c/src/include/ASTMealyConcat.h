#ifndef AST_MEALY_CONCAT_H
#define AST_MEALY_CONCAT_H

#include "ASTMealy.h"
#include <stdlib.h>

ASTMealy * createMealyConcat(ASTMealy * lMealy, ASTMealy * rMealy);

#endif