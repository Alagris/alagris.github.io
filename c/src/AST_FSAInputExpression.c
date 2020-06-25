#include "AST_FSAInputExpression.h"

AST_FSA * createFSAInputExpression(AST_FSA * lFSA, AST_FSA * rFSA) {
	AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
	node->type = FSA_INPUT_EXPRESSION;
	node->fsaInputExpression = (AST_FSAInputExpression) {lFSA, rFSA};
	return node;
}