%{
  #include "syntax_parser.h"
%}
%x string temporal
%%
[ \t\v\f]   	         ;
[;\n]  				         { return EOL; }
\"|\'					         BEGIN(string);
:		     			         { return COLON; }
\(						         { return L_PARENTHESIS; }
\)						         { return R_PARENTHESIS; }
\|						         { return PIPE; }
\<						         BEGIN(temporal);
=							         { return EQUALS; }
\*						         { return ASTERIKS; }
,							         { return COMMA; }
[A-z0-9_]+   	         { yylval.sval = strdup(yytext); return ID; }
<temporal>[A-Z]        { yylval.sval = strdup(yytext); return TEMPORAL_OPERATOR; }
<temporal>\>  				 BEGIN(INITIAL);
<string>\\n            { return NEW_LINE; }
<string>\\\\           { return BACK_SLASH; }
<string>\\\"           { return S_QUOTE; }
<string>\\\'           { return S_APOSTROPHE; }
<string>[^"]*	         { yylval.sval = strdup(yytext); return STRING; }
<string>\"|\'	         BEGIN(INITIAL);
%%
/*
\[						         BEGIN(set);
\]						         BEGIN(INITIAL);
*/
