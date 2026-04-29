#!/bin/bash
# deploy.sh — Linux / macOS / Cloud Shell one-click deploy to Firebase Hosting.
#
# Steps:
#   1. Flatten the student-ID tree into deploy/public via build.sh
#   2. Push deploy/public to site `clean-our-sea` under project
#      irl-status-window.
#
# Requires: firebase CLI installed (`npm i -g firebase-tools`) and logged in
#   (`firebase login`). First run also needs the site created in the Firebase
#   Console (irl-status-window -> Hosting -> "Add another site" -> id
#   "clean-our-sea"), then:
#     firebase target:apply hosting clean-our-sea clean-our-sea
#
# Usage:
#   chmod +x deploy.sh      # first time only
#   ./deploy.sh
#
# After it finishes, open https://clean-our-sea.web.app

set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

echo ""
echo "=== [1/2] building deploy/public ==="
bash "$ROOT/build.sh"

echo ""
echo "=== [2/2] firebase deploy --only hosting:clean-our-sea ==="
firebase deploy --only hosting:clean-our-sea

echo ""
echo "=== done ==="
echo "Open https://clean-our-sea.web.app"
