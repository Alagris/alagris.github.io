#include "FSAConcat.h"

FSAConcat * createFSAConcat(void * fsaKleeneClousure) {
	FSAConcat * fsaConcat = (FSAConcat *) malloc(sizeof(FSAConcat));
	fsaConcat->fsaKleeneClousure = fsaKleeneClousure;
	fsaConcat->next = NULL;
}

void addToFSAConcat(FSAConcat * list, void * fsaKleeneClousure) {
	while(list->next != NULL) {
		list = list->next;
	}
	list->next = createFSAConcat(fsaKleeneClousure);
}