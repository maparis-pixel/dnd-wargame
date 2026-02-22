@echo off
echo Ejecutando Test Simple de D&D Wargames...
echo ==========================================
echo.

cd /d "%~dp0"

REM Ejecutar SimpleTest
echo Ejecutando SimpleTest...
java -cp src com.dnd.wargames.test.SimpleTest

echo.
echo Presiona cualquier tecla para continuar...
pause > nul

echo.
echo Ejecutando BasicTest...
java -cp src com.dnd.wargames.test.BasicTest

echo.
echo Presiona cualquier tecla para continuar...
pause > nul

echo.
echo Ejecutando Demo de Combate...
java -cp src com.dnd.wargames.demo.CombatDemo

echo.
echo Presiona cualquier tecla para continuar...
pause > nul

echo.
echo Ejecutando CLI Interactivo...
java -cp src com.dnd.wargames.cli.CommandLineInterface

echo.
echo ¡Sistema D&D Wargames completado!
pause