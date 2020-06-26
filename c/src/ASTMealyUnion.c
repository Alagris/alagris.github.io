#include "ASTMealyUnion.h"

ASTMealy * createMealyUnion(ASTMealy * lMealy, ASTMealy * rMealy) {
	ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
	node->type = MEALY_UNION;
	node->mealy.mealyUnion.lmealy = lMealy;
	node->mealy.mealyUnion.rmealy = rMealy;
	// node->mealyUnion = (ASTMealyUnion) {lMealy, rMealy};
	return node;
}