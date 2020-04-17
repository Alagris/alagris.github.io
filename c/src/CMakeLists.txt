cmake_minimum_required(VERSION 3.10)

# set the project name
project(MealyCompiler LANGUAGES C VERSION 0.1 )

find_package(BISON)
find_package(FLEX)




BISON_TARGET(SyntaxParser syntax_parser.y ${CMAKE_CURRENT_BINARY_DIR}/syntax_parser.c)
FLEX_TARGET(SyntaxScanner syntax_scanner.l  ${CMAKE_CURRENT_BINARY_DIR}/syntax_scanner.c)
ADD_FLEX_BISON_DEPENDENCY(SyntaxScanner SyntaxParser)


# add the executable
add_executable(mealy
	mealy.c
	${BISON_SyntaxParser_OUTPUTS}
    ${FLEX_SyntaxScanner_OUTPUTS}
)


configure_file("${CMAKE_CURRENT_SOURCE_DIR}/include/config.h.in" "${CMAKE_CURRENT_SOURCE_DIR}/include/config.h")

target_include_directories(mealy PUBLIC "${CMAKE_CURRENT_SOURCE_DIR}/include")

set_property(TARGET mealy PROPERTY C_STANDARD 11)

TARGET_LINK_LIBRARIES(mealy l)

set(CMAKE_VERBOSE_MAKEFILE ON)

get_target_property(OUT mealy LINK_LIBRARIES)
message(STATUS ${OUT})

