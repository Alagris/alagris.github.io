%{
		#include "parser.h"
		#include "syntax_parser.h"
		#include <stdlib.h>
%}
%x	string temporal range definition
%%
[	\t\v\f]							;
<definition>[	\t\v\f]				;
<definition>[;\n]					BEGIN(INITIAL)
<definition>\"|\'					BEGIN(string);
<definition>:						{ return COLON; }
<definition>\(						{ return L_PARENTHESIS; }
<definition>\)						{ return R_PARENTHESIS; }
<definition>\|						{ return PIPE; }
<definition>\<						BEGIN(temporal);
=									{ return EQUALS; }
<definition>\*						{ return ASTERIKS; }
<definition>,						{ return COMMA; }
<definition>\x5b					BEGIN(range);
\<-									{ return ALPHABET_OP; }
->									{ return JUDGEMENTS_OP; }
<definition>[A-Za-z0-9_]+			{ yylval.sAST_FSA = createFSAID(strdup(yytext)); return ID; }
[A-Za-z0-9_]+						{ yylval.sstring = strdup(yytext); return ID_DEF; }
<temporal>[A-Z]						{ yylval.sstring = strdup(yytext); return TEMPORAL_OPERATOR; }
<temporal>\>						BEGIN(definition);
<string>\\n							{ return NEW_LINE; }
<string>\\x[0-9A-Fa-f]{1,2}			{ yylval.schar = (char) strtoul(yytext + 2, NULL, 16); return HEX_CHAR; }
<string>\\\\						{ return BACK_SLASH; }
<string>\\\"						{ return S_QUOTE; }
<string>\\\'						{ return S_APOSTROPHE; }
<string>[^"]*						{ yylval.sstring = strdup(yytext); return STRING; }
<string>\"|\'						BEGIN(definition);
<range>\]							BEGIN(definition);
<range>\\\]							{ return R_R_BRACKET; }
<range>\\\\							{ return R_BACK_SLASH; }
<range>\\-							{ return R_DASH_CHAR; }
<range>-							{ return R_DASH; }
<range>\\x[0-9A-Fa-f]{1,2}			{ yylval.schar = (char) strtoul(yytext + 2, NULL, 16); return R_HEX_CHAR; }
<range>[^\\\-\]]					{ return R_CHAR; }
%%
