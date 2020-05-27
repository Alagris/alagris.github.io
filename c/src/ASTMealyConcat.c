#include "ASTMealyConcat.h"

ASTMealy * createMealyConcat(ASTMealy * lMealy, ASTMealy rMealy) {
	ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
	node->type = MEALY_CONCAT;
	node->mealyConcat = {lMealy, rMealy};
	return node;
}