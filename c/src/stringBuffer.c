#include "parser.h"

void initStringBuffer(char * buffer) {
    free(buffer);
    buffer = malloc(sizeof(char));
    buffer[0] = '\0';
}

char * addStringToBuffer(char * buffer, char * string) {
    buffer = (char *) realloc(buffer, strlen(buffer) + strlen(string) + 1);
    if(!buffer) {
        exit(1);
    }
    strcat(buffer, string);
    return buffer;
}

char * addCharToBuffer(char * buffer, char character) {
    size_t len = strlen(buffer);
    buffer = (char *) realloc(buffer, len + sizeof(char) + 1);
    if(!buffer) {
        exit(1);
    }
    buffer[len] = character;
    buffer[len + 1] = '\0';
    return buffer;
}