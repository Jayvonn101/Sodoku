#!/bin/bash
javac -cp lib/junit-platform-console-standalone-1.11.4.jar:bin \
  -d bin \
  src/tests/Game_Generator/SodokuTest.java

java -jar lib/junit-platform-console-standalone-1.11.4.jar \
  --class-path bin \
  --scan-class-path
