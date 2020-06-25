#ifndef PARSER_H
#define PARSER_H

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

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
#include "AST_FSAID.h"
#include "mocks.h"

int yyparse();

void initStringBuffer(char * buffer);
char * addStringToBuffer(char * buffer, char * string);
char * addCharToBuffer(char * buffer, char character);

#endif