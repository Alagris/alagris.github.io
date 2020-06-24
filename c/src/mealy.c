/* Temp file struct - will be refactored in the future */

#include <stdio.h>
#include "data.h"
#include "AST.h"


void parseATatT(char * source, struct Mealy * output){
	
}

void parseGlushkov(char * source, struct Mealy * output){
	
}


void lossyDeterminization(struct Mealy * input, struct FunctionalMealy * output){
	
}

size_t count(ASTMealy * mealy){
    // switch(mealy->type){
    // case 0:
    //     return count(root->uni.lhs)+count(root->uni.rhs);
    // case 1:
    //     return count(root->concat.lhs)+count(root->concat.rhs);
    // case 2:
    //     return count(root->kleene.child);
    // case 3:
    //     return count(root->output.child);
    // case 4:
    //     return 1;
    // case 5:
    //     return 0;
    // }
    printf("XD\n");
}

Mealy * defineFunctionF(char * id, LiteralList * params, ASTMealy * astMealy) {
    size_t sigmaSize = count(astMealy);
    // char * stack = malloc(sizeof(char)* sigmaSize);
    // localize($1,0,stack);
    // struct T t = f($1,sigmaSize);
    // bisonOutput = TtoM(&t,stack,sigmaSize);
    // freeTContents(&t,sigmaSize);
    // free(stack);
}

Mealy * defineSimpleMealy(char * id, ASTMealy * astMealy) {

}
