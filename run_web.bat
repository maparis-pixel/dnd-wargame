@echo off
setlocal EnableExtensions

cd /d "%~dp0"
set "PID_FILE=.webserver.pid"
set "JAVA_CMD=java"
set "JAVAC_CMD=javac"

if exist "%ProgramFiles%\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe" (
    set "JAVA_CMD=%ProgramFiles%\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe"
)
if exist "%ProgramFiles%\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\javac.exe" (
    set "JAVAC_CMD=%ProgramFiles%\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\javac.exe"
)

if exist "%ProgramFiles%\Eclipse Adoptium\jdk-25\bin\java.exe" (
    set "JAVA_CMD=%ProgramFiles%\Eclipse Adoptium\jdk-25\bin\java.exe"
)
if exist "%ProgramFiles%\Eclipse Adoptium\jdk-25\bin\javac.exe" (
    set "JAVAC_CMD=%ProgramFiles%\Eclipse Adoptium\jdk-25\bin\javac.exe"
)

"%JAVA_CMD%" -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] java no esta en PATH.
    exit /b 1
)

"%JAVAC_CMD%" -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] javac no esta en PATH.
    exit /b 1
)

if exist "%PID_FILE%" (
    set /p EXISTING_PID=<"%PID_FILE%"
    if not "%EXISTING_PID%"=="" (
        tasklist /FI "PID eq %EXISTING_PID%" | findstr /R /C:" %EXISTING_PID% " >nul
        if not errorlevel 1 (
            echo [INFO] El servidor ya esta ejecutandose con PID %EXISTING_PID%.
            echo [INFO] URL: http://localhost:8080
            exit /b 0
        )
    )
    del "%PID_FILE%" >nul 2>&1
)

if not exist bin mkdir bin

dir /s /b src\com\dnd\wargames\*.java > .sources.list
"%JAVAC_CMD%" -d bin @.sources.list
set "COMPILE_EXIT=%ERRORLEVEL%"
del .sources.list >nul 2>&1

if not "%COMPILE_EXIT%"=="0" (
    echo [ERROR] Error de compilacion.
    exit /b 1
)

for /f %%I in ('powershell -NoProfile -Command "$p=Start-Process -FilePath '%JAVA_CMD%' -ArgumentList '-cp','bin','com.dnd.wargames.DndWargames','web' -WorkingDirectory '.' -PassThru; $p.Id"') do set "SERVER_PID=%%I"

if "%SERVER_PID%"=="" (
    echo [ERROR] No se pudo iniciar el servidor web.
    exit /b 1
)

> "%PID_FILE%" echo %SERVER_PID%

echo [OK] Servidor web iniciado con PID %SERVER_PID%.
echo [OK] URL: http://localhost:8080
exit /b 0
