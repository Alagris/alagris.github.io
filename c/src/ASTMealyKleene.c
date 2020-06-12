#include "ASTMealyKleene.h"

ASTMealy * createMealyKleene(ASTMealy * mealy) {
	ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
	node->type = MEALY_KLEENE;
	node->mealyUnion = (ASTMealyUnion) {mealy};
	return node;
}
