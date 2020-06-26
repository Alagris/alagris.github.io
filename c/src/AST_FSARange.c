#include "AST_FSARange.h"

AST_FSA * createFSARange(char beg, char end) {
	AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
	node->type = FSA_RANGE;
	node->fsa.fsaRange.beg = beg;
	node->fsa.fsaRange.end = end;
	// node->fsaRange = (AST_FSARange) {beg, end};
	return node;
}