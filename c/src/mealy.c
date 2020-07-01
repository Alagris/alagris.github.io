/* Temp file struct - will be refactored in the future */

#include <stdio.h>
#include "data.h"
#include "AST.h"
#include "mealy.h"


void addToMealyList(MealyList ** list, M * mealy, char * id) {
    while(*list) {
        list = &((*list)->next);
    }
    *list = createMealyList(mealy, id);
}

MealyList * createMealyList(M * mealy, char * id) {
    MealyList * list = malloc(sizeof(MealyList));
    list->mealy = mealy;
    list->id = id;
    list->next = NULL;
}

void addToASTMealyList(ASTMealyList ** list, ASTMealy * mealy, char * id, StringList * args) {
    while(*list) {
        list = &((*list)->next);
    }
    *list = createASTMealyList(mealy, id, args);
}

ASTMealyList * createASTMealyList(ASTMealy * mealy, char * id, StringList * args) {
    ASTMealyList * list = malloc(sizeof(ASTMealyList));
    list->mealy = mealy;
    list->id = id;
    list->args = args;
    list->next = NULL;
    return list;
}

size_t count(AST_FSA * root) {
    // AST_FSA * root = mealy->mealy.mealyAtomic.input;
    switch(root->type) {
    case FSA_ATOMIC:
        return 1;
    case FSA_UNION:
        return count(root->fsa.fsaUnion.lFSA) + count(root->fsa.fsaUnion.rFSA);
    case FSA_CONCAT:
        return count(root->fsa.fsaConcat.lFSA) + count(root->fsa.fsaConcat.rFSA);
    case FSA_KLEENE:
        return count(root->fsa.fsaKleene.fsa);
    // case FSA_RANGE:
    //     return 1; // ranges to-do
    case MEALY_PHANTOM:
        return count(root->fsa.mealyPhantom.in);
    case FSA_EPS:
        return 0;
    }
}

int localize(AST_FSA * root, int offset, char * stack) {
    switch(root->type) {
    case FSA_ATOMIC:
        stack[offset] = root->fsa.fsaAtomic.letter;
        root->fsa.fsaAtomic.letter = (char) offset;
        return offset + 1;
    case FSA_UNION:
        return localize(root->fsa.fsaUnion.lFSA,
            localize(root->fsa.fsaUnion.rFSA, offset, stack), stack);
    case FSA_CONCAT:
        return localize(root->fsa.fsaConcat.lFSA,
            localize(root->fsa.fsaConcat.rFSA, offset, stack), stack);
    case FSA_KLEENE:
        return localize(root->fsa.fsaKleene.fsa, offset, stack);
    case FSA_RANGE:
        return 0; // Ranges to-do
    case MEALY_PHANTOM:
        return localize(root->fsa.mealyPhantom.in, offset, stack);
    case FSA_EPS:
        return offset;
    }
}

char *** empty2D(int size) {
    char *** n = malloc(sizeof(char**)*size);
    for(int i = 0;i<size;i++){
        n[i] = malloc(sizeof(char*)*size);
        for(int j = 0;j<size;j++){
            n[i][j] = NULL;
        }
    }
    return n;
}

char ** setConcatStr(char ** x, int size, char * y) {
    char ** n = malloc(sizeof(char*)*size);
    for(int i = 0;i<size;i++){
        n[i] = concat(x[i],y);
    }
    return n;
}

char ** strConcatSet(char * x, int size, char ** y) {
    char ** n = malloc(sizeof(char*)*size);
    for(int i = 0;i<size;i++){
        n[i] = concat(x,y[i]);
    }
    return n;
}

char *** concatProd(char ** x, char ** y, int size) {
    char *** n = malloc(sizeof(char**)*size);
    for(int i = 0;i<size;i++){
        n[i] = malloc(sizeof(char*)*size);
    }
    for(int i = 0;i<size;i++){
        for(int j = 0;j<size;j++){
            n[i][j] = concat(x[i],y[j]);
        }
    }
    return n;
}

char ** singleton(char inputSymbol, char * outputString, int sigmaSize) {
    char ** n = empty(sigmaSize);
    n[inputSymbol] = outputString;
    return n;
}

char * concat(char * x, char * y) {
    if(x==NULL || y==NULL)return NULL;
    char * n = malloc(sizeof(char) * (strlen(x) + strlen(y) + 1));
    strcpy(n,x);
    strcat(n,y);
    return n;
}

char * epsilon() {
    char * n = malloc(sizeof(char));
    n[0]='\0';
    return n;
} 

char ** empty(int sigmaSize) {
    char ** n = calloc(sigmaSize, sizeof(char *));
    return n;
}

char* unionSingleton(char * lhs, char * rhs) {
    if(lhs) {
        if(rhs) {
            fprintf(stderr, "Nondeterminism!\n");
            exit(1);
        } else {
            return lhs;
        }
    }else {
        return rhs;    
    }
}
char ** unionInPlaceLhs(char ** lhs, char ** rhs,int size) {
    for(int i=0; i < size; ++i) {
        lhs[i]=unionSingleton(lhs[i], rhs[i]);
    }
    return lhs;
}
char *** union2DInPlaceLhs(char *** lhs, char *** rhs,int size) {
    for(int i=0; i< size; ++i) {
        for(int j=0; j<size; ++j) {
            lhs[i][j]=unionSingleton(lhs[i][j],rhs[i][j]);
        }
    }
    return lhs;
}

T f(AST_FSA * root, int sigmaSize) {
    switch(root->type) {
        case FSA_ATOMIC: {
            T x;
            x.l = empty2D(sigmaSize);
            x.b = singleton(root->fsa.fsaAtomic.letter, epsilon(), sigmaSize);
            x.e = singleton(root->fsa.fsaAtomic.letter, epsilon(), sigmaSize);
            x.a = NULL;
            return x;
        }
        case FSA_UNION: {
            T x = f(root->fsa.fsaUnion.lFSA, sigmaSize);
            T y = f(root->fsa.fsaUnion.rFSA, sigmaSize);
            x.l = union2DInPlaceLhs(x.l, y.l, sigmaSize);
            x.e = unionInPlaceLhs(x.e, y.e, sigmaSize);
            x.b = unionInPlaceLhs(x.b, y.b, sigmaSize);
            x.a = unionSingleton(x.a,y.a);
            free2DShallow(y.l,sigmaSize);
            free(y.e);
            free(y.b);
            return x;
        }
        case FSA_CONCAT: {
            T x = f(root->fsa.fsaConcat.lFSA, sigmaSize);
            T y = f(root->fsa.fsaConcat.rFSA, sigmaSize);
            char *** oldxl = x.l;
            x.l = union2DInPlaceLhs(concatProd(x.e, y.b, sigmaSize), union2DInPlaceLhs(x.l, y.l, sigmaSize), sigmaSize);
            char ** oldxb = x.b;
            x.b = unionInPlaceLhs(strConcatSet(x.a, sigmaSize, y.b), x.b, sigmaSize);
            char ** oldxe = x.e;
            x.e = unionInPlaceLhs(setConcatStr(x.e, sigmaSize, y.a), y.e, sigmaSize);
            x.a = concat(x.a,y.a);
            free2DShallow(oldxl, sigmaSize);
            free2DShallow(y.l, sigmaSize);
            free(y.e);
            free1D(y.b, sigmaSize);
            free1D(oldxe, sigmaSize);
            free(oldxb);
            return x;
        }
        case FSA_KLEENE: {
            T x = f(root->fsa.fsaKleene.fsa, sigmaSize);
            if(x.a && x.a[0]!='\0'){
                fprintf(stderr, "Nondeterminism!\n");
                exit(-6);
            }
            char *** oldxl = x.l;
            x.l = union2DInPlaceLhs(concatProd(x.e, x.b, sigmaSize), x.l, sigmaSize);
            free2DShallow(oldxl,sigmaSize);
            return x;
        }
        case MEALY_PHANTOM: {
            //T x = f(root->fsa.fsaInputExpression.lFSA, sigmaSize);
            T x = f(root->fsa.mealyPhantom.in, sigmaSize);
            char ** oldxe = x.e;
            x.e = setConcatStr(x.e,sigmaSize, root->fsa.mealyPhantom.out);
            free1D(oldxe, sigmaSize);
            char * oldxa = x.a;
            x.a = concat(x.a, root->fsa.mealyPhantom.out);
            free(oldxa);
            return x;
        }
        case FSA_EPS: {
            T x;
            x.l = empty2D(sigmaSize);
            x.b = empty(sigmaSize);
            x.e = empty(sigmaSize);
            x.a = epsilon();
            return x;
        }
    }
}

MealyList * compileMealy(ASTMealyList * mealyList) {
    MealyList * mealyMList = NULL;
    while(mealyList) {
        if(!mealyList->args) {
            AST_FSA * fsa = mealyList->mealy->mealy.mealyAtomic.input;
            size_t sigmaSize = count(fsa);
            char * stack = malloc(sigmaSize);
            localize(fsa, 0, stack);
            T t = f(fsa, sigmaSize);
            //printT(&t, sigmaSize);
            M * tmpM = malloc(sizeof(M));
            *tmpM = TtoM(&t, stack, sigmaSize);
            addToMealyList(&mealyMList, tmpM, mealyList->id);
            freeTContents(&t, sigmaSize);
        }
        mealyList = mealyList->next;
    }
    return mealyMList;
}

void free1D(char ** mat, int size){
    for(int i=0; i<size; i++){
        free(mat[i]);
    }
    free(mat);
}

void free2D(char *** mat, int size){
    for(int i=0; i<size; i++){
        free1D(mat[i], size);
    }
    free(mat);
}

void free2DShallow(char *** mat, int size){
    for(int i=0; i < size; ++i){
        free(mat[i]);
    }
    free(mat);
}

void freeMContents(struct M * m){
    free1D(m->F, m->stateCount);
    for(int i=0; i < m->stateCount; ++i) {
        for(int j = 0; j < m->delta[i].len; ++j) {
            free(m->delta[i].ts[j].output);
        }
        free(m->delta[i].ts);
    }
    free(m->delta);
}

char* outputFor(struct M * m, int sourceState, int targetState, int inputSymbol){
    for(int tran=0; tran < m->delta[sourceState].len; ++tran) {
        if(m->delta[sourceState].ts[tran].input == inputSymbol  &&
            m->delta[sourceState].ts[tran].targetState == targetState) {
            return m->delta[sourceState].ts[tran].output;
        }
    }
    return NULL;
}

void freeTContents(struct T * t, int size){
    free(t->a);
    free1D(t->b, size);
    // free1D(t->e, size);
    free1D(t->e, 1);
    free2D(t->l, size);
}

void printT(struct T * t, int size){
    
    for(int i=0;i<size;i++){
        printf("l:");
        for(int j=0;j<size;j++){
            printf("'%s' ",t->l[i][j]);
        }
        printf("\n");
    }
    printf("b:");
    for(int i=0;i<size;i++){
        printf("'%s' ",t->b[i]);
    }
    printf("\n");
    printf("e:");
    for(int i=0;i<size;i++){
        printf("'%s'(%p) ", t->e[0], t->e[0]);
        // printf("'%s'(%p) ", t->e[i], t->e[i]);
    }
    printf("\n");
    printf("a:'%s'\n",t->a);
}

char * run(struct M * m, char * input){
    int len = strlen(input);
    //we will use backtracking mechanism for
    //evaluation of all nondeterministic
    //superpositions of automaton
    int backtrack[len + 1][m->stateCount];
    for(int state=0; state < m->stateCount; ++state)
        for(int step=0; step < len + 1; ++step)
            backtrack[step][state] = -1;
    backtrack[0][m->i] =  m->i;
    //first we need to propagate input forwards for each symbol 
    for(int step=1;step<=len;step++){
        char inputSymbol = input[step-1];
        for(int state=0; state < m->stateCount; ++state) {
            if(backtrack[step-1][state] > -1) {
                for(int tran=0; tran < m->delta[state].len; ++tran) {
                    // remember that there might me multiple nondeterministic
                    // transitions for given state and inputSymbol
                    if(m->delta[state].ts[tran].input == inputSymbol) {
                        int targetState = m->delta[state].ts[tran].targetState;
                        if(backtrack[step][targetState] > -1) {
                            fprintf(stderr, "Nondeterminism at step %d in state %d",step,targetState);
                            exit(1);
                        }
                        backtrack[step][targetState] = state; // this will allow us to
                        //backtrack later and determine printed output
                    }
                }
            }
        }
    }
    //now we need to check if any of the accepting states is reached
    int acceptedState = -1;
    for(int state = 0; state < m->stateCount; ++state) {
        if(backtrack[len][state] > -1) {
            if(acceptedState == -1) {
                acceptedState = state;
            }else{
                fprintf(stderr, "Nondeterminism at final step in states %d and %d",acceptedState,state);
                exit(1);
            }
        }
    }
    if(acceptedState == -1){
        //no state accepted, so we return NULL (empty set) as output
        return NULL;
    }
    //now we need to backtrack and collect output printed along each transition
    int sizeOfOutput = strlen(m->F[acceptedState]);
    int backtrackedState = acceptedState;
    for(int step = len; step > 0; --step) {
        int sourceState = backtrack[step][backtrackedState];
        sizeOfOutput += strlen(outputFor(m, sourceState, backtrackedState, input[step - 1]));
        backtrackedState = sourceState;
    }
    char * output = malloc(sizeof(char)*sizeOfOutput+1);
    output[0]='\0';
    strcat(output,m->F[acceptedState]);
    backtrackedState = acceptedState;
    for(int step = len; step > 0; --step) {
        int sourceState = backtrack[step][backtrackedState];
        char * stepOutput = outputFor(m, sourceState, backtrackedState, input[step - 1]);
        int stepOutputLen = strlen(stepOutput);
        memmove(output + stepOutputLen, output, strlen(output) + 1);
        memcpy(output,stepOutput, stepOutputLen);
        backtrackedState = sourceState;
    }
    return output;
}

char * copyStr(char * str) {
    if(str == NULL) {
        return NULL;
    }
    char * n = malloc(sizeof(char) * (strlen(str) + 1));
    *n = '\0';
    strcat(n, str);
    return n;
}

struct M TtoM(struct T * t, char * stack, int sigmaSize){
    struct M m;
    m.stateCount = sigmaSize + 1;//there is one state for every symbols in 
    //Sigma plus one extra initial state 
    m.i = sigmaSize;
    m.F = malloc(sizeof(char*) * m.stateCount);
    for(int i=0; i < sigmaSize; ++i){
        // m.F[i] = copyStr(t->e[i]);
        m.F[i] = copyStr(t->e[0]);
    }
    m.F[sigmaSize] = copyStr(t->a);
    m.delta = malloc(sizeof(struct Transitions)*m.stateCount);
    // delta stores an array of transitions for each state
    for(int i=0; i < sigmaSize; ++i){
        int transitionCount = 0;//first find out how many outgoing
        //transitions originate in state i
        for(int j=0; j < sigmaSize; ++j) {
            if(t->l[i][j]) {
                ++transitionCount;
            }
        }
        //then allocate array of transitions
        m.delta[i].len = transitionCount;
        struct Transition * tr = m.delta[i].ts = malloc(sizeof(struct Transition) * transitionCount);
        //then collect all the transitions 
        for(int j=0, k=0; j < sigmaSize; ++j){
            char * output = copyStr(t->l[i][j]);
            if(output){
                tr[k].targetState = j; //j is the target state of transition
                tr[k].input = stack[j]; // stack[j] tells us the input label of transition
                tr[k].output = output;
                ++k;
            }
        }
    }
    int initialTransitionCount = 0;

    for(int j=0; j < sigmaSize; ++j) {
        if(t->b[j]) {
            ++initialTransitionCount;
        }
    }
    
    m.delta[sigmaSize].len = initialTransitionCount;
    struct Transition * initialTr = m.delta[sigmaSize].ts = 
        malloc(sizeof(struct Transition)*initialTransitionCount);

    for(int j=0, k=0; j < sigmaSize; ++j){
        char * output = copyStr(t->b[j]);
        if(output){
            initialTr[k].targetState = j; 
            initialTr[k].input = stack[j];
            initialTr[k].output = output;
            k++;
        }
    }
    return m;
}

void defineFunctionF(ASTMealyList ** mealyList, char * id, StringList * args, ASTMealy * astMealy) {
    addToASTMealyList(mealyList, astMealy, id, args);
}