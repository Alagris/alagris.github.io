#pragma once

#include <stdlib.h>
#include "MealyConcat.h"

typedef struct MealyUnion {
    MealyConcat * mealyContac;
    struct MealyUnion * next;
} MealyUnion;

MealyUnion * createMealyUnion(MealyConcat * mealyConcat);
void addToMealyUnion(MealyUnion * list, MealyConcat * mealyConcat);
