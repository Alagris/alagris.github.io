/*
#ifndef FSA_UNION_H
#define FSA_UNION_H
*/

#include <stdlib.h>
#include "AST.h"

typedef struct FSAUnion {
    FSAConcat * fsaConcat;
    struct FSAUnion *next;
} FSAUnion;

FSAUnion * createFSAUnion(FSAConcat * fsaConcat);
void addToFSAUnion(FSAUnion * list, FSAConcat * fsaConcat);
/*
#endif
*/