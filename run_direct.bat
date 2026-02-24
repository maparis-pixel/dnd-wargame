@echo off
setlocal EnableExtensions
echo D^&D Wargames - Ejecucion Directa
echo ================================
echo.

set "BASEDIR=%~dp0"
cd /d "%BASEDIR%"

if "%JAVA_HOME%"=="" (
    if exist "%ProgramFiles%\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\javac.exe" (
        set "JAVA_HOME=%ProgramFiles%\Eclipse Adoptium\jdk-25.0.2.10-hotspot"
    )
    if exist "%ProgramFiles%\Eclipse Adoptium\jdk-25\bin\javac.exe" (
        set "JAVA_HOME=%ProgramFiles%\Eclipse Adoptium\jdk-25"
    )
)

if "%JAVA_HOME%"=="" (
    echo [ERROR] JAVA_HOME no definido. Configuralo antes de ejecutar.
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"
set "CLASSPATH=%BASEDIR%bin"

if not exist bin mkdir bin

echo Compilando fuentes...
dir /s /b src\com\dnd\wargames\*.java > .sources.list
"%JAVA_HOME%\bin\javac.exe" -d bin @.sources.list
set "COMPILE_EXIT=%ERRORLEVEL%"
del .sources.list >nul 2>&1

if not "%COMPILE_EXIT%"=="0" (
    echo [ERROR] Error de compilacion
    pause
    exit /b 1
)

echo [OK] Compilacion exitosa
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

echo Ejecutando app (CLI)...
"%JAVA_HOME%\bin\java.exe" -cp "%CLASSPATH%" com.dnd.wargames.DndWargames

echo.
echo [OK] Completado
pause