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
}

/*%token QUOTE*/
%token COLON
%token L_PARENTHESIS
%token R_PARENTHESIS
%token PIPE
%token LT
%token GT
%token L_BRACKETS
%token R_BRACKETS
%token DASH
%token EQUALS
%token ESCAPE
%token <sval> STRING
%token <sval> NAME
%type <sval> quoted_expression
%type <sval> extended_string
%type <sval> output
%type <sval> expression
%type <sval> subexpression
%type <sval> escaped_expr
%type <sval> transition

%%
line
	: NAME EQUALS transitions {
		printf("NAME: %s\n", $1);
		free($1);
	}
	;

transitions
	: transitions PIPE transition
	| transition
	;

transition
	: expression COLON output
	| expression
	;

expression
	:	expression subexpression
	| subexpression
	;

subexpression
	: quoted_expression
	;

quoted_expression
	/*: QUOTE extended_string QUOTE {*/
	: extended_string {
		printf("quoted_expression: %s\n", $1);
	}
	;

extended_string
	: extended_string STRING
	| extended_string escaped_expr
	| escaped_expr
	| STRING {
		printf("extended_string: %s\n", $1);
	}
	;
		
escaped_expr
	: ESCAPE STRING {
		printf("escaped_expr: %s\n", $$);
	}
	;

output
	: quoted_expression {
		printf("output: %s\n", $1);
	}
	;
/*
test2
	: STRING {
		printf("string_test: %s\n", $1);
};
	

tests:
tests test | test;
test:
QUOTE |
COLON |
L_PARENTHESIS |
R_PARENTHESIS |
PIPE |
LT |
GT |
L_BRACKETS |
R_BRACKETS |
DASH |
EQUALS |
ESCAPE |
STRING
	;
*/
%%
/*
main (int argc, char **argv)
{
  FILE *input_file = fopen("/dev/stdin", "r");
  // make sure it's valid:
  if (!input_file) {
    printf("Cannot open\n");
    return -1;
  }
  // Set flex to read from it instead of defaulting to STDIN:
  yyin = input_file;

  // Parse through the input:
  yyparse();

	return 0;
}
*/

void yyerror(const char *s) {
	printf("Parse error!  Message: %s\n", s );
  exit(-1);
}
