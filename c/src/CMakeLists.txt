cmake_minimum_required(VERSION 3.10)

# set the project name
project(MealyCompiler LANGUAGES C VERSION 0.1 )

find_package(BISON)
find_package(FLEX)




BISON_TARGET(SyntaxParser syntax_parser.y ${CMAKE_CURRENT_BINARY_DIR}/syntax_parser.c
			VERBOSE bison.output)
FLEX_TARGET(SyntaxScanner syntax_scanner.l  ${CMAKE_CURRENT_BINARY_DIR}/syntax_scanner.c)
ADD_FLEX_BISON_DEPENDENCY(SyntaxScanner SyntaxParser)


# add the executable
add_executable(mealy
	mealy.c
	cli.c
	data.c
	StringList.c
	mocks.c
	mealy.c
	stringBuffer.c
	FSAFunction.c
	AST_FSA.c
	AST_FSAAtomic.c
	AST_FSAArg.c
	AST_FSAConcat.c
	AST_FSAInputExpression.c
	AST_FSAKleene.c
	AST_FSARange.c
	AST_FSAUnion.c
	ASTMealyAtomic.c
	ASTMealyConcat.c
	ASTMealyKleene.c
	ASTMealyUnion.c
	include/data.h
	include/mealy.h
	${BISON_SyntaxParser_OUTPUTS}
    ${FLEX_SyntaxScanner_OUTPUTS}
)


configure_file("${CMAKE_CURRENT_SOURCE_DIR}/include/config.h.in" "${CMAKE_CURRENT_SOURCE_DIR}/include/config.h")

target_include_directories(mealy PUBLIC "${CMAKE_CURRENT_SOURCE_DIR}/include")

set_property(TARGET mealy PROPERTY C_STANDARD 11)



if(APPLE)
    add_library(argp SHARED IMPORTED) # or STATIC instead of SHARED
	set_target_properties(argp PROPERTIES
	  IMPORTED_LOCATION "/usr/local/opt/argp-standalone/lib/libargp.a"
	  INTERFACE_INCLUDE_DIRECTORIES "/usr/local/opt/argp-standalone/include/"
	)
	
	TARGET_LINK_LIBRARIES(mealy argp)
endif()

TARGET_LINK_LIBRARIES(mealy l )


set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_BUILD_TYPE Debug)

get_target_property(OUT mealy LINK_LIBRARIES)
message(STATUS ${OUT})

