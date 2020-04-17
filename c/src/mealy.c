#include <stdio.h>

/*This is used for alphabet and indexing states. Let R be a set of integers between r_min and r_max inclusive*/

#define r long long unsigned int
#define r_max 18446744073709551615
#define r_min 0
#define r_epsilon r_max
#define r_no_state r_max
/*This is used for booleans*/
#define b unsigned char

/*Generic pair that could for instance hold indices of two vertices, or vertex and alphabet char*/

struct Transition{
	r destinationState;
	r inputLabelRangeBeginInclusive;
	r inputLabelRangeEndExclusive;
	r* output;
};

struct Mealy{
	r stateCount;
	b * acceptingStates;
	struct Transition ** delta;
	
};
struct FunctionalTransition{
	r destinationState;
	r inputLabelRangeBeginInclusive;
	r inputLabelRangeEndExclusive;
	r * cellTransitions;
	r * cellTransitionLabels;
};

struct FunctionalMealy{
	r stateCount;
	r cellsCount;
	r * acceptingCellPerState;
	
	struct FunctionalTransition ** delta;
	
};

void parseATatT(char * source, struct Mealy * output){
	
}

void parseGlushkov(char * source, struct Mealy * output){
	
}


void lossyDeterminization(struct Mealy * input, struct FunctionalMealy * output){
	
}

/*returns output length*/
int execute(struct FunctionalMealy * automaton, char * input, char * output){
	
}


