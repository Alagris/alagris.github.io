#pragma once
// #ifndef FSA_UNION_H
// #define FSA_UNION_H

#include <stdlib.h>
#include "FSAConcat.h"

typedef struct FSAUnion {
    void * fsaConcat;
    struct FSAUnion *next;
} FSAUnion;

FSAUnion * createFSAUnion(void * fsaConcat);
void addToFSAUnion(FSAUnion * list, void * fsaConcat);

// #endif