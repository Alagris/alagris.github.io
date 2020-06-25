#include "AST_FSAAtomic.h"

AST_FSA * createFSAAtomic(LiteralList * literalList) {
	AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
	node->type = FSA_ATOMIC;
	node->fsaAtomic = (AST_FSAAtomic) {literalList};
	return node;
}
