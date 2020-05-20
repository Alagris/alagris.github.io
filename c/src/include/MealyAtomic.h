#pragma once

#define MEALY_ATOMIC_KLEENE_CLOUSURE_CLOSED 1
#define MEALY_ATOMIC_KLEENE_CLOUSURE_OPENED 0

#include <stdlib.h>
#include "InputExpression.h"
#include "LiteralList.h"

typedef struct MealyAtomic {
    char isClosed;
    InputExpression * inputExpression;
    LiteralList * literalList;
} MealyAtomic;

MealyAtomic * createMealyAtomic(InputExpression * inputExpression, LiteralList * literalList);
MealyAtomic * setMealyAtomicClousure(MealyAtomic * mealyAtomic, char clousure);