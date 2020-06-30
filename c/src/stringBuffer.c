#include "parser.h"

void initStringBuffer(char * buffer) {
    free(buffer);
    buffer = malloc(sizeof(char));
    buffer[0] = '\0';
}

char * addStringToBuffer(char * buffer, char * string) {
    size_t buf_len = 0;
    if(buffer) {
        size_t buf_len = strlen(buffer);
    }
    buffer = (char *) realloc(buffer, buf_len + strlen(string) + 1);
    if(!buffer) {
        exit(1);
    }
    strcat(buffer, string);
    return buffer;
}

char * addCharToBuffer(char * buffer, char character) {
    size_t buf_len = 0;
    if(buffer) {
        size_t buf_len = strlen(buffer);
    }
    buffer = (char *) realloc(buffer, buf_len + sizeof(char) + 1);
    if(!buffer) {
        exit(1);
    }
    buffer[buf_len] = character;
    buffer[buf_len + 1] = '\0';
    return buffer;
}