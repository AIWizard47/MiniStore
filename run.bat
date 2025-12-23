@echo off
echo ==============================
echo Compiling Java files...
echo ==============================

@REM REM Create output directory if not exists
@REM if not exist out mkdir out

REM Compile all Java files into out/
javac -d out ^
src\com\sam\ministore\*.java ^
src\com\sam\components\*.java ^
src\com\sam\dataTypes\*.java ^
src\com\sam\main\Main.java

if errorlevel 1 (
    echo ❌ Compilation failed!
    pause
    exit /b
)

echo.
echo ==============================
echo Running program...
echo ==============================
echo.

java -cp out com.sam.main.Main

pause
