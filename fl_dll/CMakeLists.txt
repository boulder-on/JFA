cmake_minimum_required(VERSION 3.17)
project(foreign_link C)

set(CMAKE_C_STANDARD 99)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "../..")
add_library(passport_test SHARED library.c library.h)