@echo off
REM build.bat -- Windows port of build.sh. Flattens the student-ID authoring
REM folders into deploy\public so a static file server (python -m
REM http.server 8000) can serve the whole site as one thing.
REM
REM Usage:  build.bat
REM         (or just run serve.bat which calls this first, then starts both
REM          the combined Spring Boot backend on :8080 and the static server)
REM
REM Safe to re-run; the deploy\ folder is wiped and rebuilt every time. No
REM Firebase in the local variant — all API calls go to Spring Boot on :8080.

setlocal enabledelayedexpansion
set "ROOT=%~dp0"
set "DST=%ROOT%deploy\public"

echo.
echo [build] Rebuilding %DST% from student-ID folders...

if exist "%ROOT%deploy" rmdir /S /Q "%ROOT%deploy"
mkdir "%DST%\css\2419616"
mkdir "%DST%\css\2433841"
mkdir "%DST%\js\2419616"
mkdir "%DST%\js\2433841"
mkdir "%DST%\data\2419616"

REM ---- HTML pages (flattened to public\ root) ----
copy /Y "%ROOT%2457928-Gursaaj\index.html"      "%DST%\"              >nul
copy /Y "%ROOT%2419616-Tarun\chat.html"         "%DST%\"              >nul
copy /Y "%ROOT%2419616-Tarun\communities.html"  "%DST%\"              >nul
copy /Y "%ROOT%2419616-Tarun\community.html"    "%DST%\"              >nul
copy /Y "%ROOT%2419616-Tarun\README.md"         "%DST%\"              >nul
copy /Y "%ROOT%2433841-Tala\leaderboard.html"   "%DST%\"              >nul
copy /Y "%ROOT%2335984-Daniel\frontend\quizzes.html"   "%DST%\"      >nul
copy /Y "%ROOT%2335984-Daniel\frontend\quiz-page.html" "%DST%\"      >nul
copy /Y "%ROOT%2335984-Daniel\frontend\logo.png"       "%DST%\"      >nul
copy /Y "%ROOT%2457928-Gursaaj\game.html"       "%DST%\"              >nul
copy /Y "%ROOT%2457928-Gursaaj\login.html"      "%DST%\"              >nul
copy /Y "%ROOT%2457928-Gursaaj\register.html"   "%DST%\"              >nul
copy /Y "%ROOT%2457928-Gursaaj\news.html"       "%DST%\"              >nul
copy /Y "%ROOT%2457928-Gursaaj\profile.html"    "%DST%\"              >nul

REM ---- CSS ----
copy /Y "%ROOT%2419616-Tarun\css\communities.css"     "%DST%\css\2419616\" >nul
copy /Y "%ROOT%2419616-Tarun\css\communities.css.min" "%DST%\css\2419616\" >nul
copy /Y "%ROOT%2433841-Tala\css\leaderboard.css"      "%DST%\css\2433841\" >nul
copy /Y "%ROOT%2433841-Tala\css\leaderboard.css.min"  "%DST%\css\2433841\" >nul
copy /Y "%ROOT%2433841-Tala\css\style.css"            "%DST%\css\"         >nul
copy /Y "%ROOT%2335984-Daniel\frontend\quizzstyle.css" "%DST%\"           >nul
copy /Y "%ROOT%2335984-Daniel\frontend\quiz-page.css" "%DST%\"            >nul
copy /Y "%ROOT%shared\css\enhancements.css"           "%DST%\css\"         >nul

REM ---- JavaScript ----
copy /Y "%ROOT%2419616-Tarun\js\chat.js"         "%DST%\js\2419616\" >nul
copy /Y "%ROOT%2419616-Tarun\js\communities.js"  "%DST%\js\2419616\" >nul
copy /Y "%ROOT%2433841-Tala\js\leaderboard.js"   "%DST%\js\2433841\" >nul
copy /Y "%ROOT%2457928-Gursaaj\js\auth.js"       "%DST%\js\"         >nul
copy /Y "%ROOT%2457928-Gursaaj\js\game.js"       "%DST%\js\"         >nul
copy /Y "%ROOT%2457928-Gursaaj\js\login.js"      "%DST%\js\"         >nul
copy /Y "%ROOT%2457928-Gursaaj\js\news.js"       "%DST%\js\"         >nul
copy /Y "%ROOT%2457928-Gursaaj\js\register.js"   "%DST%\js\"         >nul
copy /Y "%ROOT%2457928-Gursaaj\js\profile.js"    "%DST%\js\"         >nul
copy /Y "%ROOT%shared\js\effects.js"             "%DST%\js\"         >nul
copy /Y "%ROOT%shared\js\enhancements.js"        "%DST%\js\"         >nul
REM quizzes.js + quiz-page.js live at public\ root (not inside public\js\) -- matches how quizzes.html/quiz-page.html reference them
copy /Y "%ROOT%2335984-Daniel\frontend\quizzes.js"   "%DST%\"       >nul
copy /Y "%ROOT%2335984-Daniel\frontend\quiz-page.js" "%DST%\"       >nul

REM ---- Data ----
copy /Y "%ROOT%2419616-Tarun\data\communities.json" "%DST%\data\2419616\" >nul

REM ---- May's pollution map (vanilla HTML + Leaflet, hosted at \may\) ----
REM Note: May replaced the earlier React/Vite app with a single static
REM map.html using Leaflet. No build step needed.
set "MAY_SRC=%ROOT%2431260-May\Frontend"
if exist "%MAY_SRC%\map.html" (
    if not exist "%DST%\may" mkdir "%DST%\may"
    copy /Y "%MAY_SRC%\map.html"  "%DST%\may\" >nul
    copy /Y "%MAY_SRC%\style.css" "%DST%\may\" >nul
    echo [build] May's map deployed to %DST%\may\ map.html + style.css
) else (
    echo [build] skipping May's map no map.html at %MAY_SRC%
)

REM ---- Count result ----
set "COUNT=0"
for /R "%DST%" %%F in (*) do set /A COUNT+=1
echo [build] OK: !COUNT! files written to %DST%

endlocal
exit /b 0
