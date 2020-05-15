#pragma once
// #ifndef FSA_KLEENE_CLOUSURE_H
// #define FSA_KLEENE_CLOUSURE_H

#include "FSA.h"

typedef struct FSAKleeneClousure {
    char isClosed;
    // change to a non-void pointer
    void * fsa;
} FSAKleeneClousure;

// #endif