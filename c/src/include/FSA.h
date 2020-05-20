#pragma once
// #ifndef FSA_H
// #define FSA_H

#include "FSAUnion.h"
#include "LiteralList.h"

typedef struct FSA {
    LiteralList * literalList;
    void * fsaUnion;
} FSA;

// #endif