#pragma once

#include <stdlib.h>
#include "FSA.h"

typedef struct InputExpression {
    void * fsa;
    struct InputExpression * next;
} InputExpression;

InputExpression * createInputExpression(void * fsa);
void addToInputExpression(InputExpression * list, void * fsa);