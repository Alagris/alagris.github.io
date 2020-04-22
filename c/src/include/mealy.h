
#ifndef MEALY_H
#define MEALY_H
#include "data.h"


void parseATatT(char * source, struct Mealy * output);

void parseGlushkov(char * source, struct Mealy * output);


void lossyDeterminization(struct Mealy * input, struct FunctionalMealy * output);

/*returns output length*/
int execute(struct FunctionalMealy * automaton, char * input, char * output);

#endif /* MEALY_H */
