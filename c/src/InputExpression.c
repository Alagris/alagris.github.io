#include "InputExpression.h"

InputExpression * createInputExpression(AST_FSA * fsa) {
	InputExpression * inputExpression = (InputExpression *) malloc(sizeof(InputExpression));
	inputExpression->fsa = fsa;
	inputExpression->next = NULL;
	return inputExpression;
}

void addToInputExpression(InputExpression * list, AST_FSA * fsa) {
	while(list->next != NULL) {
		list = list->next;
	}
	list->next = createInputExpression(fsa);
}