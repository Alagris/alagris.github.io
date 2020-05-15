#pragma once

#include <stdlib.h>
#include "MealyAtomic.h"

typedef struct MealyConcat {
    MealyAtomic * mealyAtomic;
    struct MealyConcat * next;
} MealyConcat;

MealyConcat * createMealyConcat(MealyAtomic * mealyAtomic);
void addToMealyConcat(MealyConcat * list, MealyAtomic * mealyAtomic);
