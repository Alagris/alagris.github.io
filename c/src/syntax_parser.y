%{
	#include <stdio.h>
	#include <stdlib.h>
	#include "AST.h"

  extern int yylex();
  extern int yyparse();
  extern FILE *yyin;
//  extern int yydebug=1;
 
  void yyerror(const char *s);

%}


%union {
  char * sstring;
  LiteralList * sLiteralList;
  AST_FSA * sAST_FSA;
  ASTMealy * sASTMealy;
  InputExpression * sInputExpression;
  char schar;
}

%token COLON
%token L_PARENTHESIS
%token R_PARENTHESIS
%token PIPE
%token LT
%token GT
%token DASH
%token EQUALS
%token BACK_SLASH
%token NEW_LINE
%token S_QUOTE
%token S_APOSTROPHE
%token EOL
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
%token <schar> HEX_CHAR
%token <sstring> STRING
%token <sAST_FSA> ID
%token <sstring> TEMPORAL_OPERATOR
%type <schar> escaped_char
%type <schar> range_literal
%type <sstring> string_atomic
%type <sLiteralList> string_literal
%type <sAST_FSA> fsa_Kleene_clousure
%type <sAST_FSA> input_atomic
%type <sInputExpression> input_expression
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

%%
defs
	: defs def
	| def
	;

def
	: ID EQUALS mealy {
			printf("%s: OK\n", $1);
			free($1);
		}
	| ID L_PARENTHESIS params R_PARENTHESIS EQUALS mealy {
			printf("%s: OK\n", $1);
			free($1);
		}
	| ID ALPHABET_OP range {
			printf("%s: OK\n", $1);
			free($1);
		}
	| ID ALPHABET_OP enum_alphabet {
			printf("%s: OK\n", $1);
			free($1);
		}
	| ID COLON judgements {
			printf("%s: OK\n", $1);
			free($1);
		}
	| EOL
	| /* empty */
	;

judgements
	: judgements JUDGEMENTS_OP ID
	| ID
	;

params
	: params COMMA ID
	| ID
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
			$$ = createMealyAtomic((InputExpression *) $1, (LiteralList *) $3);
		}
	| input_expression {
			$$ = createMealyAtomic((InputExpression *) $1, (LiteralList *) NULL);
		}
	;

input_expression
	: input_expression input_atomic {
			addToInputExpression((InputExpression *) $$, $2);
		}
	| input_atomic {
			$$ = createInputExpression($1);
		}
	;

input_atomic
	: ID { $$ = createMockFSA(); }
	| function { $$ = createMockFSA(); }
	| temporal_expression { $$ = createMockFSA(); }
	| fsa
	| range { $$ = createMockFSA(); }
	;

range
	: range_literal R_DASH range_literal
	;

range_literal
	: R_HEX_CHAR
	| R_R_BRACKET { $$ = ']'; }
	| R_DASH_CHAR { $$ = '-'; }
	| R_BACK_SLASH { $$ = '\\'; }
	| R_CHAR
	;

function
	: ID L_PARENTHESIS param_values R_PARENTHESIS
	;

temporal_expression
	: TEMPORAL_OPERATOR L_PARENTHESIS param_values R_PARENTHESIS
	;

param_values
	: param_values COMMA input_atomic
	| input_atomic
	;

fsa
	: string_literal { 
			$$ = createFSAAtomic((LiteralList *) $1);
		}
	| L_PARENTHESIS fsa_union R_PARENTHESIS
	;

fsa_union
	: fsa_union PIPE fsa_concat {
			createFSAConcat((AST_FSA *) $$, $3);
		}
	| fsa_concat
	;

fsa_concat
	: fsa_concat fsa_Kleene_clousure {
			createFSAConcat((AST_FSA *) $$, $2);
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
	: string_literal string_atomic {
			addToLiteralList(((LiteralList *) $$), $2);
		}
	| string_atomic {
			$$ = createLiteralList($1);
		}
	;

string_atomic
	:	STRING
	| escaped_char { 
			$$ = (char *) malloc(sizeof(char));
			*($$) = $1;
		}
	;

escaped_char
	: NEW_LINE { $$ = '\n'; }
	| BACK_SLASH { $$ = '\\'; }
	| S_QUOTE { $$ = '\"'; }
	| S_APOSTROPHE { $$ = '\''; }
	| HEX_CHAR
	;
%%

void yyerror(const char *s) {
	printf("Parse error!  Message: %s\n", s );
	exit(-1);
}
