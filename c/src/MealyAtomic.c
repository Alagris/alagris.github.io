#include "MealyAtomic.h"

MealyAtomic * createMealyAtomic(InputExpression * inputExpression, LiteralList * literalList) {
    MealyAtomic * mealyAtomic = (MealyAtomic *) malloc(sizeof(MealyAtomic));

    mealyAtomic->inputExpression = inputExpression;
    if (literalList) {
        mealyAtomic->literalList=literalList;
    }
    return mealyAtomic;
}

MealyAtomic * setMealyAtomicClousure(MealyAtomic * mealyAtomic, char clousure) {
    mealyAtomic->isClosed = clousure;
    return mealyAtomic;
}