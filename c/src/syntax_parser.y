%{
	#include "parser.h"

  extern int yylex();
  extern int yyparse();
  extern FILE *yyin;
  //extern functions *;
//  extern int yydebug=1;
 
  void yyerror(const char *s);

%}

%union {
  char * sstring;
  LiteralList * sLiteralList;
  AST_FSA * sAST_FSA;
  ASTMealy * sASTMealy;
  char schar;
}

%token COLON
%token PERCENT
%token L_PARENTHESIS
%token R_PARENTHESIS
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
%token <schar> R_HEX_CHAR
%token <schar> R_CHAR
%token <sstring> STRING
%token <sAST_FSA> ID
%token <sstring> ID_DEF
%token <sstring> TEMPORAL_OPERATOR
%type <schar> range_literal
%type <sLiteralList> string_literal
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
%type <sLiteralList> params
%type <sAST_FSA> param_values
%type <sAST_FSA> temporal_expression 
%%
defs
	: defs def
	| def
	;

def
	: ID_DEF EQUALS mealy {
			defineSimpleMealy((char *) $1, (ASTMealy *) $3);
		}
	| ID_DEF L_PARENTHESIS params R_PARENTHESIS EQUALS mealy {
			defineFunctionF((char *) $1, (LiteralList *) $3, (ASTMealy *) $6);
		}
	| ID_DEF ALPHABET_OP range
	| ID_DEF ALPHABET_OP enum_alphabet
	| ID_DEF COLON judgements
	;

judgements
	: judgements JUDGEMENTS_OP ID_DEF
	| ID_DEF
	;

params
	: params COMMA ID_DEF {
			addToLiteralList(((LiteralList *) $$), (char *) $3);
		}
	| ID_DEF {
			$$ = createLiteralList((char *) $1);
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
	: mealy_union PIPE mealy_concat {
			createMealyUnion((ASTMealy *) $$, (ASTMealy *) $3);
		}
	| mealy_concat
	;

mealy_concat
	: mealy_concat mealy_Kleene_closure {
			createMealyConcat((ASTMealy *) $$, (ASTMealy *) $2);
		}
	| mealy_Kleene_closure
	;

mealy_Kleene_closure
	: L_PARENTHESIS mealy_atomic R_PARENTHESIS ASTERIKS {
			$$ = createMealyKleene((ASTMealy *) $2);
		}
	| mealy_atomic
	;

mealy_atomic
	: input_expression COLON string_literal {
			$$ = createMealyAtomic((AST_FSA *) $1, (LiteralList *) $3);
		}
	| input_expression PERCENT {
			$$ = createMealyAtomic((AST_FSA *) $1, (LiteralList *) NULL);
		}
	;

input_expression
	: input_expression input_atomic {
			$$ = createFSAInputExpression((AST_FSA *) $$, (AST_FSA *) $2);
		}
	| input_atomic
	;

input_atomic
	: function
//	| temporal_expression
//	| ID
	| fsa
	| range
	;

range
	: range_literal R_DASH range_literal {
		$$ = createFSARange((char) $1, (char) $3);
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
	: ID L_PARENTHESIS param_values R_PARENTHESIS {
		$$ = evalF((AST_FSAID *) $1, (AST_FSA *) $3);
	}
	;

temporal_expression
	: TEMPORAL_OPERATOR L_PARENTHESIS param_values R_PARENTHESIS {
		$$ = createMockFSA();
		}
	;

param_values
	: param_values COMMA input_atomic
	| input_atomic
	;

fsa
	: string_literal { 
			$$ = createFSAAtomic((LiteralList *) $1);
		}
	| L_PARENTHESIS fsa_union R_PARENTHESIS {
		$$ = $2;
	}
	;

fsa_union
	: fsa_union PIPE fsa_concat {
			createFSAUnion((AST_FSA *) $$, (AST_FSA *) $3);
		}
	| fsa_concat
	;

fsa_concat
	: fsa_concat fsa_Kleene_clousure {
			createFSAConcat((AST_FSA *) $$, (AST_FSA *) $2);
		}
	| fsa_Kleene_clousure
	;

fsa_Kleene_clousure
	: L_PARENTHESIS input_atomic R_PARENTHESIS ASTERIKS {
			$$ = createFSAKleene((AST_FSA *) $2);
		}
	| input_atomic
	;

string_literal
	: string_literal STRING {
			addToLiteralList(((LiteralList *) $$),(char *) $2);
		}
	| STRING {
			$$ = createLiteralList((char *) $1);
		}
	;

%%

void yyerror(const char *s) {
	printf("Parse error!  Message: %s\n", s );
	exit(-1);
}
