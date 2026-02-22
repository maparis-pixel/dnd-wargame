@echo off
setlocal EnableExtensions

cd /d "%~dp0"
set "PID_FILE=.webserver.pid"

if not exist "%PID_FILE%" (
    echo [INFO] No habia PID de servidor para detener.
    exit /b 0
)

set /p PID=<"%PID_FILE%"
del "%PID_FILE%" >nul 2>&1

if "%PID%"=="" (
    echo [INFO] No habia PID de servidor para detener.
    exit /b 0
)

taskkill /PID %PID% /T /F >nul 2>&1
tasklist /FI "PID eq %PID%" | findstr /R /C:" %PID% " >nul

if errorlevel 1 (
    echo [OK] Servidor detenido ^(PID %PID%^).
) else (
    echo [INFO] No se pudo confirmar la detencion del PID %PID%.
)

exit /b 0
