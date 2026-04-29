@echo off
REM undeploy.bat - Windows one-click "take the site offline".
REM
REM Runs: firebase hosting:disable --site clean-our-sea
REM
REM Effect: the site stops serving content (visitors get a Firebase
REM "site not found" page) but your config, site ID, and deploy history
REM are all preserved. To bring it back online, run deploy.bat again.
REM
REM Usage:
REM   undeploy.bat
setlocal
cd /d "%~dp0"

echo.
echo === firebase hosting:disable --site clean-our-sea ===
call firebase hosting:disable --site clean-our-sea -f
if errorlevel 1 (
    echo.
    echo !! firebase hosting:disable failed.
    echo    - Are you logged in?  firebase login:list
    echo    - Does site clean-our-sea exist under project irl-status-window?
    exit /b 1
)

echo.
echo === done ===
echo Site clean-our-sea is now offline. Run deploy.bat to bring it back.
endlocal
