cmake_minimum_required(VERSION 3.10)

# set the project name
project(MealyCompiler LANGUAGES C VERSION 0.1 )

# add the executable
add_executable(mealy mealy.c)


configure_file("${CMAKE_CURRENT_SOURCE_DIR}/include/config.h.in" "${CMAKE_CURRENT_SOURCE_DIR}/include/config.h")

target_include_directories(mealy PUBLIC "${CMAKE_CURRENT_SOURCE_DIR}/include")

set_property(TARGET mealy PROPERTY C_STANDARD 11)
