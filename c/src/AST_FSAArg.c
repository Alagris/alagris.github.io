#include "AST_FSAArg.h"

AST_FSA * createFSAArg(char * arg) {
    AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
    node->type = FSA_ARG;
    node->fsa.fsaArg.arg = arg;
    return node;
}