#define FSA_UNION 1
#define FSA_CONCAT 2
#define FSA_KLEENE 3

#ifndef AST_FSA_H
#define AST_FSA_H

#include "AST_FSAUnion.h"
#include "AST_FSAConcat.h"
#include "AST_FSAKleene.h"

typedef struct AST_FSA{
	char type;
	union{
		struct AST_FSAUnion fsaUnion;
		struct AST_FSAConcat fsaConcat;
		struct AST_FSAKleene fsaKleene;
	};
} AST_FSA;

#endif