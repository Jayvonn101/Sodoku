@echo off
setlocal EnableDelayedExpansion

echo Building Sudoku...

:: Create target directory if it doesn't exist
if not exist target\classes mkdir target\classes

:: Find all Java files and compile them
set "JAVA_FILES="
for %%f in (src\main\java\org\sudoku\*.java) do (
    set "JAVA_FILES=!JAVA_FILES! %%f"
)

:: Compile Java files
javac -d target\classes -sourcepath src\main\java !JAVA_FILES!

if %errorlevel% neq 0 (
    echo Build failed!
    exit /b 1
)

echo Build successful!
echo Classes compiled to: target\classes
