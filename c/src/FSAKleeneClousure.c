#include "FSAKleeneClousure.h"

FSAKleeneClousure * createKleeneClousure(void * fsa, char isClosed) {

    FSAKleeneClousure * fsaKleeneClousure = (FSAKleeneClousure *) malloc(sizeof(FSAKleeneClousure));

    if(isClosed) {
        fsaKleeneClousure->isClosed = FSA_KLEENE_CLOUSURE_CLOSED;
    } else {
        fsaKleeneClousure->isClosed = FSA_KLEENE_CLOUSURE_OPENED;
    }
    fsaKleeneClousure->fsa = (FSA *) fsa;

    return fsa;
}