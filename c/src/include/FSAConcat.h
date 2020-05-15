#pragma once
// #ifndef FSA_CONCAT_H
// #define FSA_CONCAT_H

#include <stdlib.h>
#include "FSAKleeneClousure.h"

typedef struct FSAConcat {
    FSAKleeneClousure * fsaKleeneClousure;
    struct FSAConcat * next;
} FSAConcat;

FSAConcat * createFSAConcat(FSAKleeneClousure * fsaKleeneClousure);
void addToFSAConcat(FSAConcat * list, FSAKleeneClousure * fsaKleeneClousure);

// #endif