#pragma once
// #ifndef FSA_CONCAT_H
// #define FSA_CONCAT_H

#include <stdlib.h>
#include "FSAKleeneClousure.h"

typedef struct FSAConcat {
    void * fsaKleeneClousure;
    struct FSAConcat * next;
} FSAConcat;

FSAConcat * createFSAConcat(void * fsaKleeneClousure);
void addToFSAConcat(FSAConcat * list, void * fsaKleeneClousure);

// #endif