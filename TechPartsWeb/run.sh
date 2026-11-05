#!/usr/bin/env bash
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out src/*.java && java -Dfile.encoding=UTF-8 -cp out Main
