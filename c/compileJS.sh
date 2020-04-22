#!/bin/bash
if [ ! -e bin ];then
  mkdir bin
  cd bin
  ./emconfigure cmake ../src
  cd ..
fi 

cd bin
cmake --build .


