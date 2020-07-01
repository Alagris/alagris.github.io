#include "FSAFunction.h"

AST_FSA * evalF(struct ASTMealyList * mealyList, char * funId, AST_FSAList * args) {
    char found = 0;
    while(mealyList) {
        if(!strcmp(funId, mealyList->id)) {
            found = 1;
            break;
        }
        mealyList = mealyList->next;
    }
    if(!found) {
        exit(3);
    }

    return _evalF(mealyList->mealy->mealy.mealyAtomic.input, args, mealyList->args);
}

AST_FSA * _evalF(AST_FSA * root, AST_FSAList * argsValues, StringList * argsNames) {
    switch(root->type) {
        case FSA_ATOMIC:
            return createFSAAtomic(root->fsa.fsaAtomic.letter);
        case FSA_UNION:
            return createFSAUnion((AST_FSA *) _evalF(root->fsa.fsaUnion.lFSA, argsValues, argsNames),
                                    (AST_FSA *) _evalF(root->fsa.fsaUnion.rFSA, argsValues, argsNames));
        case FSA_CONCAT:
            return createFSAConcat((AST_FSA *) _evalF(root->fsa.fsaConcat.lFSA, argsValues, argsNames),
                                    (AST_FSA *) _evalF(root->fsa.fsaConcat.rFSA, argsValues, argsNames));
        case FSA_KLEENE:
            return createFSAKleene((AST_FSA *) _evalF(root->fsa.fsaKleene.fsa, argsValues, argsNames));
        // case FSA_RANGE:
        //     return createFSARange((AST_FSA *) _evalF(root->fsa.fsaRange.beg, argsValues, argsNames),
        //                             (AST_FSA *) _evalF(root->fsa.fsaRange.end, argsValues, argsNames));
        case FSA_INPUT_EXPRESSION:
            return createFSAInputExpression((AST_FSA *) _evalF(root->fsa.fsaInputExpression.lFSA, argsValues, argsNames),
                                    (AST_FSA *) _evalF(root->fsa.fsaInputExpression.rFSA, argsValues, argsNames));
        case FSA_ARG: ;
            int index = 0;
            while(argsNames) { 
                if(!strcmp(argsNames->string, root->fsa.fsaArg.arg)) {
                    for(int i = 0; i != index; ++i) {
                        argsValues = argsValues->next;
                    }
                    return argsValues->fsa;
                }
                ++index;
                argsNames = argsNames->next;
            }
            exit(-4);
        case FSA_EPS:
            return createFSAEpsilon();
        case MEALY_PHANTOM:
            return _evalF(root->fsa.mealyPhantom.in, argsValues, argsNames);
    } 
}