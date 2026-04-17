@echo off
REM serve.bat -- one-shot local runner for clean-our-sea-local (Windows).
REM
REM What it does, in order:
REM   1. Rebuild deploy\public (runs build.bat)
REM   2. Start the combined Spring Boot backend on :8080 in a new window
REM   3. Start Python's static server on :8000 for deploy\public in *this*
REM      window (so Ctrl-C here stops the frontend; close the backend window
REM      to stop that)
REM
REM Data persists in %USERPROFILE%\cleanoursea.mv.db between runs (H2 file
REM mode). Delete that file to wipe the DB; data.sql will reseed users on
REM the next boot.
REM
REM Usage: just double-click serve.bat, or run it from a cmd prompt.
REM Then visit http://localhost:8000/index.html

setlocal
set "ROOT=%~dp0"
set "BACKEND_DIR=%ROOT%backend"
set "PUBLIC_DIR=%ROOT%deploy\public"

echo.
echo [1/3] Rebuilding deploy\public ...
call "%ROOT%build.bat"
if errorlevel 1 (
    echo [!] build.bat failed.
    exit /b 1
)

if not exist "%BACKEND_DIR%\mvnw.cmd" (
    echo [!] missing %BACKEND_DIR%\mvnw.cmd
    exit /b 1
)

echo.
echo [2/3] Starting Spring Boot backend on :8080 in a new window ...
echo       ^(close that window when you're done to stop the backend^)
start "clean-our-sea backend" cmd /k "cd /d %BACKEND_DIR% && mvnw.cmd spring-boot:run"

REM Give Spring Boot a few seconds to boot before the static server takes over.
timeout /t 5 /nobreak >nul

echo.
echo [3/3] Starting static server on :8000 for %PUBLIC_DIR%
echo       Open http://localhost:8000/index.html
echo       Ctrl-C here stops the frontend; close the backend window to stop that.
cd /d "%PUBLIC_DIR%"
python -m http.server 8000

endlocal
