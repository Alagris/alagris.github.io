#include "FSA.h"

FSA * createFSAWithLiteral(LiteralList * literalList) {
    FSA * fsa = (FSA *) malloc(sizeof(FSA));
    fsa->literalList = literalList;
    return fsa;
}

FSA * createFSAWithUnion(void * fsaUnion) {
    FSA * fsa = (FSA *) malloc(sizeof(FSA));
    fsa->fsaUnion = fsaUnion;
    return fsa;
}