/* Temp file struct - will be refactored in the future */

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

#include <stdlib.h>
#include <argp.h>

extern FILE *yyin;
static char doc[] = "Description";
static char args_doc[] = "INPUT_FILE (default stdin)";

static struct argp_option options[] = {
  {"verbose",  'v', 0,      0,  "Produce verbose output" },
  {"quiet",    'q', 0,      0,  "Don't produce any output" },
  {"output",   'o', "FILE", 0,
   "Output to FILE instead of standard output" },
  { 0 }
};

/* Used by main to communicate with parse_opt. */
struct arguments
{
  char *args[1];                /* arg1 & arg2 */
  int silent, verbose;
  char *output_file;
};

/* Parse a single option. */
static error_t
parse_opt (int key, char *arg, struct argp_state *state)
{
  /* Get the input argument from argp_parse, which we
     know is a pointer to our arguments structure. */
  struct arguments *arguments = state->input;

  switch (key)
    {
    case 'q': case 's':
      arguments->silent = 1;
      break;
    case 'v':
      arguments->verbose = 1;
      break;
    case 'o':
      arguments->output_file = arg;
      break;
    case 'i':
      arguments->output_file = arg;
      break;

    case ARGP_KEY_ARG:
      if (state->arg_num >= 3)
        /* Too many arguments. */
        argp_usage (state);

      arguments->args[state->arg_num] = arg;

      break;

    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}

/* Our argp parser. */
static struct argp argp = { options, parse_opt, args_doc, doc };

int
main (int argc, char **argv)
{
  struct arguments arguments;

  /* Default values. */
  arguments.verbose = 0;
  arguments.output_file = "stdout";
  arguments.args[0] = "/dev/stdin";

  /* Parse our arguments; every option seen by parse_opt will
     be reflected in arguments. */
  argp_parse (&argp, argc, argv, 0, 0, &arguments);

	/*
  printf ("INPUT_FILE = %s\nOUTPUT_FILE = %s\n"
          "VERBOSE = %s\n",
          arguments.args[0], arguments.output_file,
          arguments.verbose ? "yes" : "no");
	*/

  // open a file handle to a particular file:
  FILE *input_file = fopen(arguments.args[0], "r");
  // make sure it's valid:
  if (!input_file) {
    printf("Cannot open %s\n", arguments.args[0]);
    return -1;
  }
  // Set flex to read from it instead of defaulting to STDIN:
  yyin = input_file;

  // Parse through the input:
  yyparse();

	return 0;
}
