#pragma once
// #ifndef FSA_H
// #define FSA_H

#include "FSAUnion.h"

typedef struct FSA {
    LiteralList * literalList;
    FSAUnion * fsaUnion;
} FSA;

// #endif