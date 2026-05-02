#!/bin/bash
# Build script for Distributed Name Service
# Creates bin directory and compiles all Java source files

mkdir -p bin
javac -d bin src/Group_SourceCode/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "Run the application with one of these commands:"
    echo "  java -cp bin Group_SourceCode.Main              (interactive menu)"
    echo "  java -cp bin Group_SourceCode.Main server"
    echo "  java -cp bin Group_SourceCode.Main client [host] [port]"
else
    echo "Compilation failed!"
    exit 1
fi
