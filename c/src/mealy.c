/* Temp file struct - will be refactored in the future */

#include <stdio.h>
#include "data.h"
#include "AST.h"
#include "mealy.h"

/*
void parseATatT(char * source, struct Mealy * output){
	
}

void parseGlushkov(char * source, struct Mealy * output){
	
}


void lossyDeterminization(struct Mealy * input, struct FunctionalMealy * output){
	
}
*/

void addToASTMealyList(ASTMealyList * list, ASTMealy * mealy, char * id, StringList * args) {
    while(list->mealy) {
        list = list->next;
    }

    list = createASTMealyList(mealy, id, args);
}

ASTMealyList * createASTMealyList(ASTMealy * mealy, char * id, StringList * args) {
    ASTMealyList * list = malloc(sizeof(ASTMealyList));
    list->mealy = mealy;
    list->id = id;
    list->args = args;
    list->next = NULL;
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

void defineFunctionF(ASTMealyList * mealyList, char * id, StringList * args, ASTMealy * astMealy) {
    addToASTMealyList(mealyList, astMealy, id, args);
}