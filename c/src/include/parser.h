#ifndef PARSER_H
#define PARSER_H

#include "AST_FSA.h"
#include "AST_FSAAtomic.h"
#include "AST_FSAConcat.h"
#include "AST_FSAKleene.h"
#include "AST_FSAUnion.h"
#include "ASTMealy.h"
#include "ASTMealyAtomic.h"
#include "ASTMealyConcat.h"
#include "ASTMealyKleene.h"
#include "ASTMealyUnion.h"
#include "AST_FSAInputExpression.h"
#include "AST_FSARange.h"

int yyparse();

#endif