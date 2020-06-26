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

    return _evalF(mealyList->mealy->mealy.mealyAtomic.input, args, mealyList->args, (char) !(args == 0));
}

AST_FSA * _evalF(AST_FSA * root, AST_FSAList * argsValues, StringList * argsNames, char eval) {
    switch(root->type) {
        case FSA_ATOMIC:
            return createFSAAtomic(root->fsa.fsaAtomic.letter);
        case FSA_UNION:
            return createFSAUnion((AST_FSA *) _evalF(root->fsa.fsaUnion.lFSA, argsValues, argsNames, eval),
                                    (AST_FSA *) _evalF(root->fsa.fsaUnion.rFSA, argsValues, argsNames, eval));
        case FSA_CONCAT:
            return createFSAConcat((AST_FSA *) _evalF(root->fsa.fsaConcat.lFSA, argsValues, argsNames, eval),
                                    (AST_FSA *) _evalF(root->fsa.fsaConcat.rFSA, argsValues, argsNames, eval));
        case FSA_KLEENE:
            return createFSAKleene((AST_FSA *) _evalF(root->fsa.fsaKleene.fsa, argsValues, argsNames, eval));
        // case FSA_RANGE:
        //     return createFSARange((AST_FSA *) _evalF(root->fsa.fsaRange.beg, argsValues, argsNames, eval),
        //                             (AST_FSA *) _evalF(root->fsa.fsaRange.end, argsValues, argsNames, eval));
        case FSA_INPUT_EXPRESSION:
            return createFSAInputExpression((AST_FSA *) _evalF(root->fsa.fsaInputExpression.lFSA, argsValues, argsNames, eval),
                                    (AST_FSA *) _evalF(root->fsa.fsaInputExpression.rFSA, argsValues, argsNames, eval));
        case FSA_ARG:
            if(eval) {
                int index = -1;
                while(argsNames) { 
                    if(!strcmp(argsNames->string, root->fsa.fsaArg.arg)) {
                        return _evalF(root, NULL, NULL, 0);
                    }
                    ++index;
                    argsNames = argsNames->next;
                }
                exit(-4);
            } else {
                return root;
            }
        case FSA_EPS:
            return createFSAEpsilon();
    } 
}