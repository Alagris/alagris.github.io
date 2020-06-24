#ifndef AST_FSA_H
#define AST_FSA_H

#define FSA_ATOMIC 0
#define FSA_UNION 1
#define FSA_CONCAT 2
#define FSA_KLEENE 3
#define FSA_RANGE 4
#define FSA_INPUT_EXPRESSION 5
#define FSA_ID 6

#include "LiteralList.h"
#include "stdint.h"

struct AST_FSA;

typedef struct AST_FSAConcat {
    struct AST_FSA * lFSA;
    struct AST_FSA * rFSA;
} AST_FSAConcat;

typedef struct AST_FSAAtomic {
    LiteralList * literalList;
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

typedef struct AST_FSAID {
	char * id;
} AST_FSAID;


typedef struct AST_FSA{
	char type;
	union{
		struct AST_FSAUnion fsaUnion;
		struct AST_FSAConcat fsaConcat;
		struct AST_FSAKleene fsaKleene;
		struct AST_FSAAtomic fsaAtomic;
		struct AST_FSARange fsaRange;
		struct AST_FSAInputExpression fsaInputExpression;
		struct AST_FSAID fsaID;
	};
} AST_FSA;

#endif