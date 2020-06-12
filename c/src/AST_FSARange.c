#include "AST_FSARange.h"

AST_FSA * createFSAConcat(c * l, c * r) {
	AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
	node->type = FSA_RANGE;
	node->fsaRange = (AST_FSARange) {l, r};
	return node;
}