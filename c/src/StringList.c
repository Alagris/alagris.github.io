#include "StringList.h"

StringList * createStringList(char * string) {
	StringList * stringList = (StringList *) malloc(sizeof(StringList));
	stringList->string = string;
	stringList->next = NULL;
	return stringList;
}

void addToStringList(StringList * list, char * string) {
	while(list->next) {
		list = list->next;
	}
	list->next = createStringList(string);
}