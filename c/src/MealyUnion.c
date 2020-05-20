#include "MealyUnion.h"

MealyUnion * createMealyUnion(MealyConcat * mealyConcat) {
	MealyUnion * mealyUnion = (MealyUnion *) malloc(sizeof(MealyUnion));
	mealyUnion->mealyContac = mealyConcat;
	mealyConcat->next = NULL;
    return mealyUnion;
}
void addToMealyUnion(MealyUnion * list, MealyConcat * mealyConcat) {
	while(list->next != NULL) {
		list = list->next;
	}
	list->next = createMealyUnion(mealyConcat);
}