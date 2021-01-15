#!/bin/bash

rm -rf bin
for toml in src/test/resources/build*.toml ; do
    echo "$toml"
    file="$(basename "$toml")"
    file="${file%%.*}"
    in_file="src/test/resources/$file.input"
    out_file="src/test/resources/$file.output"
    exp_file="src/test/resources/$file.expected"
    cat "$in_file" | java -jar target/cli-2.1-jar-with-dependencies.jar run -b "$toml" > "$out_file"
    if cmp --silent "$exp_file" "$out_file" ; then
      echo "TEST $file PASSED"
    else
      diff "$exp_file" "$out_file"
      echo "TEST $file FAILED"
      exit 2
    fi


done