@echo off
REM serve.bat -- Start a local web server for this standalone slice.
REM Visit http://localhost:8000/login.html in your browser once it's running.
REM Ctrl+C in this window to stop the server.

echo Starting local server on http://localhost:8000 ...
echo Open one of these in a browser:
echo   http://localhost:8000/login.html
echo   http://localhost:8000/register.html
echo   http://localhost:8000/news.html
echo   http://localhost:8000/game.html     (needs login first)
echo.
echo Press Ctrl+C to stop.
echo.
python -m http.server 8000
pause
