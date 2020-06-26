#include "ASTMealyConcat.h"

ASTMealy * createMealyConcat(ASTMealy * lMealy, ASTMealy * rMealy) {
	ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
	node->type = MEALY_CONCAT;
	node->mealy.mealyConcat.lMealy = lMealy;
	node->mealy.mealyConcat.rMealy = rMealy;
	// node->mealyConcat = (ASTMealyConcat) {lMealy, rMealy};
	return node;
}