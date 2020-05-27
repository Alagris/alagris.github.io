#include "AST_FSAUnion.h"

AST_FSA * createFSAConcat(AST_FSA * lFSA, AST_FSA * rFSA) {
	AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
	node->type = FSA_UNION;
	node->fsaUnion = {lFSA, rFSA};
	return node;
}