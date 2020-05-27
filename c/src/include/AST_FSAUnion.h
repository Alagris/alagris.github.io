#ifndef AST_FSA_UNION_H
#define AST_FSA_UNION_H

#include "AST_FSA.h"
#include <stdlib.h>

typedef struct AST_FSAUnion {
    AST_FSA * lFSA;
    AST_FSA * rFSA;
} AST_FSAUnion;

AST_FSA * createFSAUnion(AST_FSA * lFSA, AST_FSA * rFSA);

#endif