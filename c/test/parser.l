%{
  #include "parser.tab.h"
%}
%x string
%%
[ \t\v\f\n]  		;
\"|\'					BEGIN(string);
<string>\"|\'	BEGIN(0);
:							{ /* printf("COLON\n"); */ return COLON; }
\(						{ /* printf("L_PARENTHESIS\n"); */ return L_PARENTHESIS; }
\)						{ /* printf("R_PARENTHESIS\n"); */ return R_PARENTHESIS; }
\|						{ /* printf("PIPE\n"); */ return PIPE; }
\<						{ /* printf("LT\n"); */ return LT; }
>	  					{ /* printf("GT\n"); */ return GT; }
\[						{ /* printf("L_BRACKETS\n"); */ return L_BRACKETS; }
\]						{ /* printf("R_BRACKETS\n"); */ return R_BRACKETS; }
-							{ /* printf("DASH\n"); */ return DASH; }
=							{ /* printf("EQUALS\n"); */ return EQUALS; }
\\						{ /* printf("ESCAPE\n"); */ return ESCAPE; }
[A-z0-9]+    	{ /* printf("STRING\n"); */ yylval.sval = strdup(yytext); return NAME; }
<string>[^"]*	{ /* printf("STRING\n"); */ yylval.sval = strdup(yytext); return STRING; }
%%
/*
\"|\'					{ return QUOTE; }
*/
