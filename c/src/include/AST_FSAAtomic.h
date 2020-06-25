#ifndef AST_FSA_ATOMIC_H
#define AST_FSA_ATOMIC_H

#include "AST_FSA.h"
#include "LiteralList.h"
#include <stdlib.h>

AST_FSA * createFSAAtomic(LiteralList * literalList);

#endif