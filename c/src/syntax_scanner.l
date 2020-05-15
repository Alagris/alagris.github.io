%{
		#include "AST.h"
		#include "syntax_parser.h"
		#include <stdlib.h>
%}
%x	string	temporal range
%%
[	\t\v\f]							;
[;\n]								{ return EOL; }
\"|\'								BEGIN(string);
:									{ return COLON; }
\(									{ return L_PARENTHESIS; }
\)									{ return R_PARENTHESIS; }
\|									{ return PIPE; }
\<									BEGIN(temporal);
=									{ return EQUALS; }
\*									{ return ASTERIKS; }
,									{ return COMMA; }
\x5b								BEGIN(range);
\<-									{ return ALPHABET_OP; }
->									{ return JUDGEMENTS_OP; }
[A-Za-z0-9_]+						{ yylval.sstring = strdup(yytext); return ID; }
<temporal>[A-Z]						{ yylval.sstring = strdup(yytext); return TEMPORAL_OPERATOR; }
<temporal>\>						BEGIN(INITIAL);
<string>\\n							{ return NEW_LINE; }
<string>\\x[0-9A-Fa-f]{1,2}			{ yylval.schar = (char) strtoul(yytext + 2, NULL, 16); return HEX_CHAR; }
<string>\\\\						{ return BACK_SLASH; }
<string>\\\"						{ return S_QUOTE; }
<string>\\\'						{ return S_APOSTROPHE; }
<string>[^"]*						{ yylval.sstring = strdup(yytext); return STRING; }
<string>\"|\'						BEGIN(INITIAL);
<range>\]							BEGIN(INITIAL);
<range>\\\]							{ return R_R_BRACKET; }
<range>\\\\							{ return R_BACK_SLASH; }
<range>\\-							{ return R_DASH_CHAR; }
<range>-							{ return R_DASH; }
<range>\\x[0-9A-Fa-f]{1,2}			{ yylval.schar = (char) strtoul(yytext + 2, NULL, 16); return R_HEX_CHAR; }
<range>[^\\\-\]]					{ return R_CHAR; }
%%
