#ifndef MEALY_H
#define MEALY_H
#include "data.h"
#include "LiteralList.h"
#include "ASTMealy.h"
#include "automaton.h"


void parseATatT(char * source, struct Mealy * output);

void parseGlushkov(char * source, struct Mealy * output);


void lossyDeterminization(struct Mealy * input, struct FunctionalMealy * output);

/*returns output length*/
int execute(struct FunctionalMealy * automaton, char * input, char * output);

Mealy * defineFunctionF(char * id, LiteralList * params, ASTMealy * astMealy);

Mealy * defineSimpleMealy(char * id, ASTMealy * astMealy);

size_t count(ASTMealy * mealy);

#endif /* MEALY_H */