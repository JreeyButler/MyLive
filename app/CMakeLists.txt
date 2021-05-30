cmake_minimum_required(VERSION 3.4.1)

set(cpp_dir "${cmake_source_dir}/src/main/cpp")
set(lib_dir "${cmake_source_dir}/src/main/cppLibs")

include_directories(${cpp_dir}/include)
include_directories(${cpp_dir}/libyuv/include)