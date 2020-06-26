#include "AST_FSAAtomic.h"

AST_FSA * createFSAAtomic(char letter) {
	AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
	node->type = FSA_ATOMIC;
	node->fsa.fsaAtomic.letter = letter;
	return node;
}
