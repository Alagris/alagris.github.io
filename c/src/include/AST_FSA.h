#ifndef AST_FSA_H
#define AST_FSA_H

#define FSA_ATOMIC 0
#define FSA_UNION 1
#define FSA_CONCAT 2
#define FSA_KLEENE 3
#define FSA_RANGE 4
#define FSA_INPUT_EXPRESSION 5
#define FSA_ARG 6
#define FSA_EPS 7
#define MEALY_PHANTOM 9

#include "StringList.h"
#include "stdint.h"

struct AST_FSA;

typedef struct AST_FSAConcat {
    struct AST_FSA * lFSA;
    struct AST_FSA * rFSA;
} AST_FSAConcat;

typedef struct AST_FSAAtomic {
    char letter;
} AST_FSAAtomic;

typedef struct AST_FSAKleene {
    struct AST_FSA * fsa;
} AST_FSAKleene;

typedef struct AST_FSAUnion {
    struct AST_FSA * lFSA;
    struct AST_FSA * rFSA;
} AST_FSAUnion;

typedef struct AST_FSARange {
	char beg;
	char end;
} AST_FSARange;

typedef struct AST_FSAInputExpression {
    struct AST_FSA * lFSA;
    struct AST_FSA * rFSA;
} AST_FSAInputExpression;

typedef struct AST_FSAArg {
	char * arg;
} AST_FSAArg;

typedef struct MealyPhantom {
    struct AST_FSA * in;
    char * out;
} MealyPhantom;


typedef struct AST_FSA {
	char type;
	union U_AST_FSA {
		struct AST_FSAUnion fsaUnion;
		struct AST_FSAConcat fsaConcat;
		struct AST_FSAKleene fsaKleene;
		struct AST_FSAAtomic fsaAtomic;
		struct AST_FSARange fsaRange;
		struct AST_FSAInputExpression fsaInputExpression;
		struct AST_FSAArg fsaArg;
		struct MealyPhantom mealyPhantom;
	} fsa;
} AST_FSA;

typedef struct AST_FSAList {
    struct AST_FSA * fsa;
    struct AST_FSAList *next;
} AST_FSAList;

AST_FSAList * createFSAList(AST_FSA * fsa);
void addToFSAList(AST_FSAList * list, AST_FSA * fsa);
AST_FSA * createFSAEpsilon();

#endif