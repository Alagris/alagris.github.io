#ifndef AST_FSA_ATOMIC_H
#define AST_FSA_ATOMIC_H

#include "AST_FSA.h"
#include "LiteralList.h"
#include <stdlib.h>

// typedef struct AST_FSAAtomic {
//     LiteralList * literalList;
// } AST_FSAAtomic;

AST_FSA * createFSAAtomic(LiteralList * literalList);

#endif