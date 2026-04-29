@echo off
REM deploy.bat - Windows one-click deploy to Firebase Hosting
REM   1. Flattens the student-ID tree into deploy\public via build.bat
REM   2. Pushes deploy\public to site clean-our-sea under project irl-status-window
REM
REM Requires: firebase CLI installed (npm i -g firebase-tools) and logged in
REM   (firebase login). First run also needs the site created in Firebase
REM   Console -> irl-status-window -> Hosting -> Add another site (id
REM   "clean-our-sea"), then: firebase target:apply hosting clean-our-sea clean-our-sea
REM
REM Usage:
REM   deploy.bat
REM
REM After it finishes, open https://clean-our-sea.web.app
setlocal
cd /d "%~dp0"

echo.
echo === [1/2] building deploy\public ===
call build.bat
if errorlevel 1 (
    echo.
    echo !! build.bat failed - aborting deploy.
    exit /b 1
)

echo.
echo === [2/2] firebase deploy --only hosting:clean-our-sea ===
call firebase deploy --only hosting:clean-our-sea
if errorlevel 1 (
    echo.
    echo !! firebase deploy failed.
    echo    - Are you logged in?  firebase login:list
    echo    - Is the site created? https://console.firebase.google.com/project/irl-status-window/hosting/sites
    echo    - Is the target applied? firebase target:apply hosting clean-our-sea clean-our-sea
    exit /b 1
)

echo.
echo === done ===
echo Open https://clean-our-sea.web.app
endlocal
