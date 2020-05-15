#pragma once
// #ifndef FSA_KLEENE_CLOUSURE_H
// #define FSA_KLEENE_CLOUSURE_H
#define FSA_KLEENE_CLOUSURE_CLOSED 1
#define FSA_KLEENE_CLOUSURE_OPENED 0

#include "FSA.h"

typedef struct FSAKleeneClousure {
    char isClosed;
    // change to a non-void pointer
    void * fsa;
} FSAKleeneClousure;

    // change to a non-void pointer
FSAKleeneClousure * createKleeneClousure(void * fsa, char isClosed);

// #endif