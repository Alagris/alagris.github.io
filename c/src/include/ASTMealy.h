#define MEALY_UNION 0
#define MEALY_CONCAT 1
#define MEALY_ATOMIC 2
#define MEALY_KLEENE 3

#ifndef AST_MEALY_H
#define AST_MEALY_H

#include "ASTMealyUnion.h"
#include "ASTMealyConcat.h"
#include "ASTMealyAtomic.h"
#include "ASTMealyKleene.h"

typedef struct ASTMealy{
	char type;
	union{
		struct ASTMealyUnion mealyUnion;
		struct ASTMealyConcat mealyConcat;
		struct ASTMealyAtomic mealyAtomic;
		struct ASTMealyKleene mealyKleene;
	};
} AST;

#endif