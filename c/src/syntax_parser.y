%{
  #include <stdio.h>

  extern int yylex();
  extern int yyparse();
  extern FILE *yyin;
//  extern int yydebug=1;
 
  void yyerror(const char *s);
%}
%union {
  char *sval;
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
%token <sval> STRING
%token <sval> ID
%token <sval> TEMPORAL_OPERATOR
%type <schar> escaped_char
%type <schar> range_literal

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

mealy
	: mealy_union
	;

mealy_union
	: mealy_union PIPE mealy_concat
	| mealy_concat
	;

mealy_concat
	: mealy_concat mealy_Kleene_closure
	| mealy_Kleene_closure
	;

mealy_Kleene_closure
	: L_PARENTHESIS mealy_atomic R_PARENTHESIS ASTERIKS
	| mealy_atomic
	;

mealy_atomic
	: input_expression COLON string_literal
	| input_expression
	;

input_expression
	: input_expression input_atomic
	| input_atomic
	;

input_atomic
	: ID
	| function
	| temporal_expression
	| fsa
	| range
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

enum_alphabet
	: enum_alphabet range_literal
	| range_literal
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
	: string_literal
	| L_PARENTHESIS fsa_union R_PARENTHESIS
	;

fsa_union
	: fsa_union PIPE fsa_concat
	| fsa_concat
	;

fsa_concat
	: fsa_concat fsa_Kleene_clousure
	| fsa_Kleene_clousure
	;

fsa_Kleene_clousure
	: L_PARENTHESIS input_atomic R_PARENTHESIS ASTERIKS
	| input_atomic
	;

string_literal
	: string_literal string_atomic
	| string_atomic
	;

string_atomic
	:	STRING { free($1); }
	| escaped_char
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
