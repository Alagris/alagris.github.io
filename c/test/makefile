parser.tab.c parser.tab.h: parser.y
	bison -d parser.y

lex.yy.c: parser.l parser.tab.h
	flex parser.l

main: lex.yy.c parser.tab.c parser.tab.h main.c
	gcc parser.tab.c lex.yy.c main.c -lfl -o main
