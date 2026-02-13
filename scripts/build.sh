#!/bin/bash

echo "Building Sudoku..."

# Create target directory if it doesn't exist
mkdir -p target/classes

# Compile Java files
javac -d target/classes -sourcepath src/main/java src/main/java/org/sudoku/*.java

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Build successful!"
echo "Classes compiled to: target/classes"
