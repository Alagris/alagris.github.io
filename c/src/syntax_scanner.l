%{
		#include "parser.h"
		#include "syntax_parser.h"
		static char * strbuf;
%}
%x	string temporal range definition
%%
[	\t\v\f]							;
#.*\n								;
^\s*\n								;
<definition>[	\t\v\f]				;
<definition>[;\n]					BEGIN(INITIAL);
<definition>\"|\'					{ BEGIN(string); initStringBuffer(strbuf); }
<definition>:						{ return COLON; }
<definition>%						{ return PERCENT; }
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
[A-Za-z0-9_]+						{ yylval.sstring = strdup(yytext); return ID_DEF; }
<temporal>[A-Z]						{ yylval.sstring = strdup(yytext); return TEMPORAL_OPERATOR; }
<temporal>\>						BEGIN(definition);
<string>\\n							{ strbuf = addCharToBuffer(strbuf, '\n'); }
<string>\\x[0-9A-Fa-f]{1,2}			{ strbuf = addCharToBuffer(strbuf, (char) strtoul(yytext + 2, NULL, 16)); }
<string>\\\\						{ strbuf = addCharToBuffer(strbuf, '\\'); }
<string>\\\"						{ strbuf = addCharToBuffer(strbuf, '\"'); }
<string>\\\'						{ strbuf = addCharToBuffer(strbuf, '\''); }
<string>[^"]*						{ strbuf = addStringToBuffer(strbuf, yytext); }
<string>\"|\'						{ yylval.sstring = strdup(strbuf); BEGIN(definition); return STRING; }
<range>\]							BEGIN(definition);
<range>\\\]							{ return R_R_BRACKET; }
<range>\\\\							{ return R_BACK_SLASH; }
<range>\\-							{ return R_DASH_CHAR; }
<range>-							{ return R_DASH; }
<range>\\x[0-9A-Fa-f]{1,2}			{ yylval.schar = (char) strtoul(yytext + 2, NULL, 16); return R_HEX_CHAR; }
<range>[^\\\-\]]					{ return R_CHAR; }
%%
