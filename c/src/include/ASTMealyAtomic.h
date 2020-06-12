#ifndef AST_MEALY_ATOMIC_H
#define AST_MEALY_ATOMIC_H

#include "LiteralList.h"
#include "InputExpression.h"
#include "ASTMealy.h"
#include <stdlib.h>

ASTMealy * createMealyAtomic (InputExpression * input, LiteralList * output);

#endif