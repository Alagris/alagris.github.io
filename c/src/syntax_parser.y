%{
	#include "parser.h"

  extern int yylex();
  extern int yyparse();
  extern FILE *yyin;
		// yydebug = 1;
  void yyerror(ASTMealyList ** mealyList, const char *s);

%}

%parse-param { ASTMealyList ** mealyList }

%union {
  char * sstring;
  StringList * sStringList;
  AST_FSA * sAST_FSA;
  ASTMealy * sASTMealy;
  AST_FSAList * sAST_FSAList;
  char schar;
}

%token COLON
%token PERCENT
%token L_PARENTHESIS
%token R_PARENTHESIS
%token L_ARG_PARENTHESIS
%token R_ARG_PARENTHESIS
%token PIPE
%token EQUALS
%token ASTERIKS
%token COMMA
%token R_R_BRACKET
%token R_BACK_SLASH
%token R_DASH
%token R_DASH_CHAR
%token ALPHABET_OP
%token JUDGEMENTS_OP
%token <sstring> ARG
%token <schar> R_HEX_CHAR
%token <schar> R_CHAR
%token <sstring> STRING
%token <sstring> FUN_ID
%token <sstring> ID_DEF
/* %token <sstring> TEMPORAL_OPERATOR */
%type <schar> range_literal
/* %type <sStringList> string_literal */
/* %type <sAST_FSA> mealy_phantom */
%type <sAST_FSA> fsa_Kleene_clousure
%type <sAST_FSA> input_atomic
%type <sAST_FSA> input_expression
%type <sAST_FSA> function
%type <sAST_FSA> range
%type <sAST_FSA> fsa
%type <sAST_FSA> fsa_concat
%type <sAST_FSA> fsa_union
%type <sASTMealy> mealy_atomic
%type <sASTMealy> mealy_Kleene_closure
%type <sASTMealy> mealy_concat
%type <sASTMealy> mealy_union
%type <sASTMealy> mealy
%type <sStringList> args
%type <sAST_FSAList> args_values
/* %type <sAST_FSA> temporal_expression  */
%%
defs
	: defs def
	| def
	;

def
	: ID_DEF L_ARG_PARENTHESIS args R_ARG_PARENTHESIS EQUALS mealy {
			defineFunctionF(mealyList, (char *) $1, (StringList *) $3, (ASTMealy *) $6);
		}
	| ID_DEF ALPHABET_OP range
	| ID_DEF ALPHABET_OP enum_alphabet
	| ID_DEF COLON judgements
	;

judgements
	: judgements JUDGEMENTS_OP ID_DEF
	| ID_DEF
	;

args
	: args COMMA ID_DEF {
			addToStringList(((StringList *) $1), (char *) $3);
		}
	| ID_DEF {
			$$ = createStringList((char *) $1);
		}
	| {
			$$ = (void *) NULL;
		}
	;

enum_alphabet
	: enum_alphabet range_literal
	| range_literal
	;

mealy
	: mealy_union
	;

mealy_union
	: mealy_concat
	/* : mealy_union PIPE mealy_concat {
			$$ = createMealyUnion((ASTMealy *) $1, (ASTMealy *) $3);
		} */
	;

mealy_concat
	: mealy_Kleene_closure
	/* : mealy_concat mealy_Kleene_closure {
			$$ = createMealyConcat((ASTMealy *) $1, (ASTMealy *) $2);
		} */
	;

mealy_Kleene_closure
	: mealy_atomic
	/* : L_PARENTHESIS mealy_atomic R_PARENTHESIS ASTERIKS {
			$$ = createMealyKleene((ASTMealy *) $2);
		} */
	;

/* mealy_atomic
	: mealy_phantom {
			$$ = createMealyAtomic($1, (StringList *) NULL);
		} */
mealy_atomic
	: input_expression COLON STRING {
			AST_FSA * ph = createMealyAtomicPhantom((AST_FSA *) $1, (char *) $3);
			$$ = createMealyAtomic(ph, (StringList *) NULL);
		}
	| input_expression PERCENT {
			AST_FSA * ph = createMealyAtomicPhantom((AST_FSA *) $1, (char *) NULL);
			$$ = createMealyAtomic(ph, (StringList *) NULL);
		}
	;

/* mealy_phantom
	: input_expression COLON STRING {
			$$ = createMealyAtomicPhantom((AST_FSA *) $1, (char *) $3);
		}
	| input_expression PERCENT {
			$$ = createMealyAtomicPhantom((AST_FSA *) $1, (char *) NULL);
		}
	; */

input_expression
	: input_atomic
	/* : input_expression input_atomic {
			$$ = createFSAInputExpression((AST_FSA *) $1, (AST_FSA *) $2);
		} */
	;

input_atomic
	: function
//	| temporal_expression
	| ARG {
			$$ = createFSAArg((char *) $1);
		}
	| fsa
	| range
	;

range
	: range_literal R_DASH range_literal {
		 $$ = createFSAAtomic($1);
		// $$ = createFSARange((char) $1, (char) $3);
	}
	;

range_literal
	: R_HEX_CHAR
	| R_R_BRACKET { $$ = ']'; }
	| R_DASH_CHAR { $$ = '-'; }
	| R_BACK_SLASH { $$ = '\\'; }
	| R_CHAR
	;

function
	: FUN_ID L_PARENTHESIS args_values R_PARENTHESIS {
		$$ = evalF(*mealyList, (char *) $1, (AST_FSAList *) $3);
	}
	;

/* temporal_expression
	: TEMPORAL_OPERATOR L_PARENTHESIS param_values R_PARENTHESIS {
		$$ = createMockFSA();
		}
	; */

args_values
	: args_values COMMA input_atomic {
			addToFSAList(((AST_FSAList *) $1), (AST_FSA *) $3);
		}
	| input_atomic {
			$$ = createFSAList((AST_FSA *) $1);
		}
	| { $$ = NULL; }
	;

fsa
	: fsa_union
	/* | L_PARENTHESIS fsa_union R_PARENTHESIS {
		$$ = $2;
	} */
	;

fsa_union
	: L_PARENTHESIS	fsa_union PIPE fsa_concat R_PARENTHESIS {
			$$ = createFSAUnion((AST_FSA *) $2, (AST_FSA *) $4);
		}
	| fsa_concat
	;

fsa_concat
	: fsa_concat fsa_Kleene_clousure {
			$$ = createFSAConcat((AST_FSA *) $1, (AST_FSA *) $2);
		}
	| fsa_Kleene_clousure
	;

fsa_Kleene_clousure
	: L_PARENTHESIS input_atomic R_PARENTHESIS ASTERIKS {
	/* : input_atomic ASTERIKS { */
			$$ = createFSAKleene((AST_FSA *) $2);
		}
	/* | mealy_phantom */
	| input_atomic
	| STRING { 
			$$ = createFSAEpsilon();
			char * ptr = $1;
			while(*ptr) {
				$$ = createFSAConcat((AST_FSA *) $$, createFSAAtomic(*ptr));
				++ptr;
			}
		}
	;

%%

void yyerror(ASTMealyList ** mealyList, const char *s) {
	fprintf(stderr, "Parse error!  Message: %s\n", s);
	exit(-1);
}
