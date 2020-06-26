#ifndef MEALY_H
#define MEALY_H
#include "data.h"
#include "automaton.h"
#include "parser.h"

typedef struct ASTMealyList {
    struct ASTMealy * mealy;
    char * id;
    StringList * args;
    struct ASTMealyList *next;
} ASTMealyList;

void addToASTMealyList(ASTMealyList * list, ASTMealy * mealy, char * id, StringList * args);
ASTMealyList * createASTMealyList(ASTMealy * mealy, char * id, StringList * args);

// void parseATatT(char * source, struct Mealy * output);

// void parseGlushkov(char * source, struct Mealy * output);


// void lossyDeterminization(struct Mealy * input, struct FunctionalMealy * output);

/*returns output length*/
// int execute(struct FunctionalMealy * automaton, char * input, char * output);

// Mealy * defineFunctionF(ASTMealyList * mealyList, char * id, StringList * args, ASTMealy * astMealy);
void defineFunctionF(ASTMealyList * mealyList, char * id, StringList * args, ASTMealy * astMealy);

size_t count(ASTMealy * mealy);

AST_FSA * evalF(ASTMealyList * mealyList, char * funId, AST_FSAList * args);
AST_FSA * _evalF(AST_FSA * root, AST_FSAList * argsValues, StringList * argsNames, char eval);

#endif /* MEALY_H */