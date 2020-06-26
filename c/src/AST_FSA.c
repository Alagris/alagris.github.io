#include "AST_FSA.h"

AST_FSAList * createFSAList(AST_FSA * fsa) {
	AST_FSAList * list = (AST_FSAList *) malloc(sizeof(AST_FSAList));
	list->fsa = fsa;
	list->next = NULL;
	return list;
}

void addToFSAList(AST_FSAList * list, AST_FSA * fsa) {
	while(list->next) {
		list = list->next;
	}
	list->next = createFSAList(fsa);
}