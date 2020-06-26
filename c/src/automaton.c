#include "data.h"
#include "automaton.h"

Mealy * initializeMealy(r stateCount, r acceptingStateCount) {
    // r acceptingStateBufferSize = (acceptingStateCount + sizeof(b) - 1) / sizeof(b);
    // Mealy * mealy = (Mealy *) malloc(sizeof(Mealy));
    // State * states = (State *) malloc(stateCount);
    // Transition * transitions = (Transition *) malloc(BASIC_TRANSITION_ARRAY_SIZE * sizeof(Transition));
    // states->transitions = transitions;
    // mealy->states[0] = states;
    // mealy->stateCount = 0;
    // return mealy;
}

int addState(Mealy * mealy) { 
    // State * newState = calcOffsetAndExtend(mealy->stateCount);
    // if(newState == 0)
    //     return -1;
    // mealy->stateCount += 1;
    // return 0;
}

int addTransition(Mealy * mealy, r state, t Transition,
                  r destinationState, r inputLabelRangeBeginInclusive,
                  r inputLabelRangeEndExclusive, t weight, char * output) {
//    State * state = calcStateOffset(state);
//    state[transition] = {destinationState, inputLabelRangeBeginInclusive,
//                         inputLabelRangeEndExclusive, weight, output};
}