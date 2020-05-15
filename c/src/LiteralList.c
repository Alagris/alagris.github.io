#include "LiteralList.h"

LiteralList * createLiteralList(char * string_literal) {
	LiteralList * literalList = (LiteralList *) malloc(sizeof(LiteralList));
	literalList->string = string_literal;
	literalList->next = NULL;
}

void addToLiteralList(LiteralList * literalList, char * string_literal) {
	LiteralList * list = literalList;
	while(list->next != NULL) {
		list = list->next;
	}
	list->next = createLiteralList(string_literal);
}
	
