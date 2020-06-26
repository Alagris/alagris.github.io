#ifndef AST_MEALY_ATOMIC_H
#define AST_MEALY_ATOMIC_H

#include "StringList.h"
#include "AST_FSAInputExpression.h"
#include "ASTMealy.h"
#include <stdlib.h>

ASTMealy * createMealyAtomic (AST_FSA * input, StringList * output);

#endif