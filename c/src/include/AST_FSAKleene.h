#ifndef AST_FSA_KLEENE_H
#define AST_FSA_KLEENE_H

#include "AST_FSA.h"
#include <stdlib.h>

typedef struct AST_FSAKleene {
    AST_FSA * fsa;
} AST_FSAKleene;

AST_FSA * createFSAKleene(AST_FSA * fsa);

#endif