#include "AST_FSAConcat.h"

AST_FSA * createFSAConcat(AST_FSA * lFSA, AST_FSA * rFSA) {
	AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
	node->type = FSA_CONCAT;
	node->fsaConcat = {lFSA, rFSA};
	return node;
}