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

%%
line:
	function_name EQUALS transitions

function_name:
	STRING

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
