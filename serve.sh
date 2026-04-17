#!/bin/bash
# serve.sh — one-shot local runner for clean-our-sea-local.
#
# What it does, in order:
#   1. Rebuild deploy/public (runs build.sh — flattens the student-ID tree)
#   2. Start the combined Spring Boot backend on :8080 (in the background,
#      logs to backend.log)
#   3. Start a Python static server on :8000 for deploy/public (foreground)
#
# Ctrl-C stops the static server AND the backend (the trap below kills the
# whole process group). Data persists in ~/cleanoursea.mv.db across runs
# because we use H2 in file mode (user-home path so every module shares
# the SAME file regardless of which directory you launch from). Delete
# that file to wipe the DB; data.sql will reseed users on the next boot.
#
# Usage:
#   chmod +x serve.sh      # first time only
#   ./serve.sh
#
# Then visit http://localhost:8000/index.html

set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$ROOT/backend"
PUBLIC_DIR="$ROOT/deploy/public"
LOG_FILE="$ROOT/backend.log"

echo ">> [1/3] building deploy/public ..."
bash "$ROOT/build.sh"

if [ ! -d "$BACKEND_DIR" ]; then
    echo "!! missing backend folder at $BACKEND_DIR"
    exit 1
fi

echo ">> [2/3] starting Spring Boot backend on :8080 (logs -> $LOG_FILE)"
cd "$BACKEND_DIR"

# Prefer mvnw if present; fall back to system mvn.
if [ -x "./mvnw" ]; then
    MVN="./mvnw"
else
    MVN="mvn"
fi

# Start backend in its own process group so we can kill it cleanly on Ctrl-C.
set +e
"$MVN" -q spring-boot:run > "$LOG_FILE" 2>&1 &
BACKEND_PID=$!
set -e

cleanup() {
    echo ""
    echo ">> shutting down backend (pid $BACKEND_PID) ..."
    kill "$BACKEND_PID" 2>/dev/null || true
    wait "$BACKEND_PID" 2>/dev/null || true
}
trap cleanup INT TERM EXIT

# Give Spring Boot a few seconds to come up before the static server takes
# over stdout. Not a hard dependency — if it's slow, the first few API calls
# from the browser will just fail until it's ready.
echo "   (tail -f $LOG_FILE in another terminal if you want to watch it)"
sleep 3

echo ">> [3/3] starting static server on :8000 for $PUBLIC_DIR"
cd "$PUBLIC_DIR"
echo "   open http://localhost:8000/index.html"
python3 -m http.server 8000
