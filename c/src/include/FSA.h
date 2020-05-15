/*
#ifndef FSA_H
#define FSA_H
*/

#include <stdlib.h>
#include "AST.h"

typedef struct FSA {
    LiteralList * literalList;
    FSAUnion * fsaUnion;
} FSA;

/*
#endif
*/