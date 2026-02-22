@echo off
chcp 65001 > nul
echo Compilando D^&D Wargames...
echo ==============================

REM Asegurar carpeta bin
if not exist bin mkdir bin

REM Compilar todas las clases en una sola pasada
dir /s /b src\com\dnd\wargames\*.java > .sources.list
javac -d bin @.sources.list
del .sources.list

if %errorlevel% neq 0 (
    echo [ERROR] Error de compilacion
    pause
    exit /b 1
)

echo [OK] Compilacion exitosa
echo.
echo Ejecutando tests...
echo ===================

echo Ejecutando SimpleTest...
java -cp bin com.dnd.wargames.test.SimpleTest

echo.
echo Ejecutando BasicTest...
java -cp bin com.dnd.wargames.test.BasicTest

echo.
echo Ejecutando MoraleAndWebSmokeTest...
java -cp bin com.dnd.wargames.test.MoraleAndWebSmokeTest

echo.
echo Ejecutando CombatDemo...
java -cp bin com.dnd.wargames.demo.CombatDemo

echo.
echo [OK] Todos los tests completados
pause