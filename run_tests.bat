@echo off
echo Compilando D&D Wargames...
echo ==============================

REM Compilar todas las clases
javac -d bin -cp . src/com/dnd/wargames/units/*.java
javac -d bin -cp . src/com/dnd/wargames/battle/*.java
javac -d bin -cp . src/com/dnd/wargames/cli/*.java
javac -d bin -cp . src/com/dnd/wargames/web/*.java
javac -d bin -cp . src/com/dnd/wargames/demo/*.java
javac -d bin -cp . src/com/dnd/wargames/test/*.java
javac -d bin -cp . src/com/dnd/wargames/DndWargames.java

if %errorlevel% neq 0 (
    echo ❌ Error de compilación
    pause
    exit /b 1
)

echo ✅ Compilación exitosa
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
echo ✅ Todos los tests completados
pause