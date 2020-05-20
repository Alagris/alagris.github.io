#ifndef LITERAL_LIST_H
#define LITERAL_LIST_H

#include <stdlib.h>

typedef struct LiteralList {
    char * string;
    struct LiteralList *next;
} LiteralList;

LiteralList * createLiteralList(char * string_literal);
void addToLiteralList(LiteralList * list, char * string_literal);

#endif