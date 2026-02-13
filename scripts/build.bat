@echo off
setlocal EnableDelayedExpansion

echo Building Sudoku...

:: Create target directory if it doesn't exist
if not exist target\classes mkdir target\classes

:: Compile Java files
javac -d target\classes -sourcepath src\main\java src\main\java\org\sudoku\*.java

if %errorlevel% neq 0 (
    echo Build failed!
    exit /b 1
)

echo Build successful!
echo Classes compiled to: target\classes
