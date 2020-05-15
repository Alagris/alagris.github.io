#include "LiteralList.h"

LiteralList * createList(char * string_literal) {
	LiteralList * literalList = (LiteralList *) malloc(sizeof(LiteralList));
	literalList->string = string_literal;
	literalList->next = NULL;
}

void addToList(LiteralList * literalList, char * string_literal) {
	LiteralList * list = literalList;
	while(list->next != NULL) {
		list = list->next;
	}
	list->next = createList(string_literal);
}
	
