#ifndef DATA_H
#define DATA_H
/*This is used for alphabet and indexing states. Let R be a set of integers between r_min and r_max inclusive*/

#include "stdint.h"

#define r long long unsigned int
#define r_max 18446744073709551615
#define r_min 0
#define r_epsilon r_max
#define r_no_state r_max
/*This is used for booleans*/
#define b unsigned char
#

typedef int16_t c;

/*Generic pair that could for instance hold indices of two vertices, or vertex and alphabet char*/

typedef struct Transition {
	r destinationState;
	c inputLabelRangeBeginInclusive;
	c inputLabelRangeEndExclusive;
	char * output;
} Transition;

typedef struct State
{
	Transition ** transitions;
} State;

typedef struct Mealy{
	r stateCount;
	b * acceptingStates;
	r * entryState;
	State * states;
} Mealy;


// typedef struct FunctionalTransition{
// 	r destinationState;
// 	r inputLabelRangeBeginInclusive;
// 	r inputLabelRangeEndExclusive;
// 	r * cellTransitions;
// 	r * cellTransitionLabels;
// } FunctionalTransition;

// typedef struct FunctionalMealy{
// 	r stateCount;
// 	r cellsCount;
// 	r * acceptingCellPerState;
	
// 	struct FunctionalTransition ** delta;
	
// } FunctionalMealy;

#endif
