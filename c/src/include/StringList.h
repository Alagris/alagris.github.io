#ifndef STRING_LIST_H
#define STRING_LIST_H

#include <stdlib.h>

typedef struct StringList {
    char * string;
    struct StringList *next;
} StringList;

StringList * createStringList(char * string);
void addToStringList(StringList * list, char * string);

#endif