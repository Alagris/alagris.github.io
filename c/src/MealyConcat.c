#include "MealyConcat.h"

MealyConcat * createMealyConcat(MealyAtomic * mealyAtomic) {
	MealyConcat * mealyConcat = (MealyConcat *) malloc(sizeof(MealyConcat));
	mealyConcat->mealyAtomic = mealyAtomic;
	mealyConcat->next = NULL;
    return mealyConcat;
}

void addToMealyConcat(MealyConcat * list, MealyAtomic * mealyAtomic) {
	while(list->next != NULL) {
		list = list->next;
	}
	list->next = createMealyConcat(mealyAtomic);
}