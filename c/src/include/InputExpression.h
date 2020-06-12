#pragma once

#include <stdlib.h>
#include "AST_FSA.h"

typedef struct InputExpression {
    AST_FSA * fsa;
    struct InputExpression * next;
} InputExpression;

InputExpression * createInputExpression(AST_FSA * fsa);
void addToInputExpression(InputExpression * list, AST_FSA * fsa);