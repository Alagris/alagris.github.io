#include "AST_FSAID.h"

AST_FSA * createFSAID(char * id) {
    AST_FSA * node = (AST_FSA *) malloc(sizeof(AST_FSA));
    node->type = FSA_ID;
    node->fsaID = (AST_FSAID) {id};
    return node;
}