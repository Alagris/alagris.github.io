#ifndef AST_FSA_CONCAT_H
#define AST_FSA_CONCAT_H

#include "AST_FSA.h"
#include <stdlib.h>

// typedef struct AST_FSAConcat {
//     AST_FSA * lFSA;
//     AST_FSA * rFSA;
// } AST_FSAConcat;

AST_FSA * createFSAConcat(AST_FSA * lFSA, AST_FSA * rFSA);

#endif