#include "FSAUnion.h"

FSAUnion * createFSAUnion(void * fsaConcat) {
	FSAUnion * fsaUnion = (FSAUnion *) malloc(sizeof(FSAUnion));
	fsaUnion->fsaConcat = fsaConcat;
	fsaUnion->next = NULL;
    return fsaUnion;
}

void addToFSAUnion(FSAUnion * list, void * fsaConcat) {
	while(list->next != NULL) {
		list = list->next;
	}
	list->next = createFSAUnion(fsaConcat);
}