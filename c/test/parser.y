%{
  #include <stdio.h>

  extern int yylex();
  extern int yyparse();
  extern FILE *yyin;
 
  void yyerror(const char *s);
%}
%union {
  char *sval;
}

%token QUOTE
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
	: QUOTE extended_string QUOTE
	;

extended_string
	: extended_string STRING
	| extended_string escaped_expr
	| escaped_expr
	| STRING
	;
		
escaped_expr
	: ESCAPE STRING
	;

output
	: quoted_expression
	;

/*
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

void yyerror(const char *s) {
	printf("Parse error!  Message: %s\n", s );
  exit(-1);
}
