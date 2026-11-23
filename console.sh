#!/bin/bash
set -e

rm -rf out
mkdir -p out

# Include lib/* only if a lib directory exists
if [ -d "lib" ]; then
  CP="out:lib/*"
else
  CP="out"
fi

javac -cp "$CP" -d out $(find src -name "*.java" -not -path "src/boundary/*")
java -cp "$CP" test.ConsoleBankingApp