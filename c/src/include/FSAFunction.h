#ifndef FSA_FUNCTION_H
#define FSA_FUNCTION_H

#include <stdlib.h>
#include "AST_FSA.h"

typedef struct FSAFunction
{
    char * id;
    char ** params;
    AST_FSA * body;
} FSAFunction;


AST_FSA * evalF(AST_FSAID * id, AST_FSA * params);

#endif