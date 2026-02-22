@echo off
echo D&D Wargames - Ejecucion Directa
echo ================================
echo.

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=C:\OneDrivePersonal\OneDrive\VS_Workspace\dnd_wargames\src

echo Compilando CombatDemo...
"%JAVA_HOME%\bin\javac.exe" -cp "%CLASSPATH%" "C:\OneDrivePersonal\OneDrive\VS_Workspace\dnd_wargames\src\com\dnd\wargames\demo\CombatDemo.java"

if %errorlevel% neq 0 (
    echo ❌ Error de compilacion
    pause
    exit /b 1
)

echo ✅ Compilacion exitosa
echo.

echo Ejecutando SimpleTest...
"%JAVA_HOME%\bin\java.exe" -cp "%CLASSPATH%" com.dnd.wargames.test.SimpleTest

echo.
pause

echo Ejecutando BasicTest...
"%JAVA_HOME%\bin\java.exe" -cp "%CLASSPATH%" com.dnd.wargames.test.BasicTest

echo.
pause

echo Ejecutando CombatDemo...
"%JAVA_HOME%\bin\java.exe" -cp "%CLASSPATH%" com.dnd.wargames.demo.CombatDemo

echo.
pause

echo Ejecutando CLI...
"%JAVA_HOME%\bin\java.exe" -cp "%CLASSPATH%" com.dnd.wargames.cli.CommandLineInterface

echo.
echo ¡Completado!
pause