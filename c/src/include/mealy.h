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


typedef struct T{
    char*** l;
    char** b;
    char** e;
    char* a;
} T;
struct Transition{
    char input;
    int targetState;
    char* output;
};
struct Transitions{
    struct Transition * ts;
    int len;
};
typedef struct M{
    int i;
    int stateCount;
    struct Transitions * delta;
    char ** F;
} M;

typedef struct MealyList {
    struct M * mealy;
    char * id;
    struct MealyList *next;
} MealyList;

void addToASTMealyList(ASTMealyList ** list, ASTMealy * mealy, char * id, StringList * args);
ASTMealyList * createASTMealyList(ASTMealy * mealy, char * id, StringList * args);

void addToMealyList(MealyList ** list, M * mealy, char * id);
MealyList * createMealyList(M * mealy, char * id);

void defineFunctionF(ASTMealyList ** mealyList, char * id, StringList * args, ASTMealy * astMealy);

char *** empty2D(int size);
char ** singleton(char inputSymbol, char * outputString, int sigmaSize);
char ** setConcatStr(char ** x, int size, char * y);
char ** strConcatSet(char * x, int size, char ** y);
char *** concatProd(char ** x, char ** y, int size);
char * concat(char * x, char * y);
char * epsilon();
char ** empty(int sigmaSize);
char* unionSingleton(char * lhs, char * rhs);
char ** unionInPlaceLhs(char ** lhs, char ** rhs,int size);
char *** union2DInPlaceLhs(char *** lhs, char *** rhs,int size);
T f(AST_FSA * root, int sSize);
size_t count(AST_FSA * root);
int localize(AST_FSA * root, int offset, char * stack);
MealyList * compileMealy(ASTMealyList * mealyList);
void free1D(char ** mat,int size);
void free2D(char *** mat,int size);
void free2DShallow(char *** mat,int size);
void freeMContents(struct M * m);
char* outputFor(struct M * m, int sourceState, int targetState, int inputSymbol);
void freeTContents(struct T * t,int size);
void printT(struct T * t, int size);
char * run(struct M * m, char * input);
char * copyStr(char * str);
struct M TtoM(struct T * t,char * stack, int sigmaSize);

AST_FSA * evalF(ASTMealyList * mealyList, char * funId, AST_FSAList * args);
AST_FSA * _evalF(AST_FSA * root, AST_FSAList * argsValues, StringList * argsNames);

#endif /* MEALY_H */