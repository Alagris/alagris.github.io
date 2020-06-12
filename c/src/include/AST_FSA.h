#define FSA_ATOMIC 0
#define FSA_UNION 1
#define FSA_CONCAT 2
#define FSA_KLEENE 3

#ifndef AST_FSA_H
#define AST_FSA_H

#include "LiteralList.h"

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

typedef struct AST_FSA{
	char type;
	union{
		struct AST_FSAUnion fsaUnion;
		struct AST_FSAConcat fsaConcat;
		struct AST_FSAKleene fsaKleene;
		struct AST_FSAAtomic fsaAtomic;
	};
} AST_FSA;

#endif