#include "ASTMealyKleene.h"

ASTMealy * createMealyKleene(ASTMealy * mealy) {
	ASTMealy * node = (ASTMealy *) malloc(sizeof(ASTMealy));
	node->type = MEALY_KLEENE;
	node->mealy.mealyKleene.mealy = mealy;
	// node->mealyKleene = (ASTMealyKleene) {mealy};
	return node;
}
