#include "InputExpression.h"

InputExpression * createInputExpression(void * fsa) {
	InputExpression * inputExpression = (InputExpression *) malloc(sizeof(InputExpression));
	inputExpression->fsa = fsa;
	inputExpression->next = NULL;
	return inputExpression;
}

void addToInputExpression(InputExpression * list, void * fsa) {
	while(list->next != NULL) {
		list = list->next;
	}
	list->next = createInputExpression(fsa);
}