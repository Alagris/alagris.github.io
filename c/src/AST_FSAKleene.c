#include "AST_FSAKleene.h"

AST_FSA * createFSAKleene(AST_FSA * fsa) {
    AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
    node->type = FSA_KLEENE;
    node->fsaKleene = {fsa};
    return node;
}