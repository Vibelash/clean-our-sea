#!/bin/bash
# build.sh — Flatten the student-ID authoring tree into deploy/public so a
# static file server can serve the whole site as one thing.
#
# Usage:  bash build.sh
# Then:   cd deploy/public && python3 -m http.server 8000
#         (or just run ./serve.sh from the repo root which does both steps)
#
# Re-run after any edit in a student folder. The deploy/ folder is disposable
# and re-created from scratch every time. No Firebase involved — in the
# clean-our-sea-local variant everything runs against the combined Spring Boot
# backend on :8080 instead.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SRC="$ROOT"
DST="$ROOT/deploy/public"

echo ">> building $DST from student-ID folders"

# OneDrive on Windows sometimes refuses to delete directories even when
# they're empty, so we can't rely on `rm -rf deploy` to work cleanly. Instead
# clear files first, then remove empty dirs best-effort.
find "$ROOT/deploy" -type f -delete 2>/dev/null || true
find "$ROOT/deploy" -depth -type d -empty -delete 2>/dev/null || true
mkdir -p "$DST/css/2419616" "$DST/css/2433841" \
         "$DST/js/2419616"  "$DST/js/2433841" \
         "$DST/data/2419616"

# ---- HTML pages (flattened to public/ root) ----
cp "$SRC/2457928-Gursaaj/index.html"        "$DST/"
cp "$SRC/2419616-Tarun/chat.html"           "$DST/"
cp "$SRC/2419616-Tarun/communities.html"    "$DST/"
cp "$SRC/2419616-Tarun/community.html"      "$DST/"
cp "$SRC/2419616-Tarun/README.md"           "$DST/"
cp "$SRC/2433841-Tala/leaderboard.html"     "$DST/"
cp "$SRC/2335984-Daniel/frontend/quizzes.html"   "$DST/"
cp "$SRC/2335984-Daniel/frontend/quiz-page.html" "$DST/"
cp "$SRC/2335984-Daniel/frontend/logo.png"       "$DST/"
cp "$SRC/2457928-Gursaaj/game.html"         "$DST/"
cp "$SRC/2457928-Gursaaj/login.html"        "$DST/"
cp "$SRC/2457928-Gursaaj/register.html"     "$DST/"
cp "$SRC/2457928-Gursaaj/news.html"         "$DST/"
cp "$SRC/2457928-Gursaaj/profile.html"      "$DST/"

# ---- CSS ----
# Student-ID subfolders kept (HTML references them at these paths):
cp "$SRC/2419616-Tarun/css/communities.css"     "$DST/css/2419616/"
cp "$SRC/2419616-Tarun/css/communities.css.min" "$DST/css/2419616/"
cp "$SRC/2433841-Tala/css/leaderboard.css"      "$DST/css/2433841/"
cp "$SRC/2433841-Tala/css/leaderboard.css.min"  "$DST/css/2433841/"
# Shared / top-level CSS (HTML references as css/*.css):
cp "$SRC/2433841-Tala/css/style.css"            "$DST/css/"
# Daniel's quizzes.html links quizzstyle.css; quiz-page.html links quiz-page.css.
# Both live at the root next to the HTML files:
cp "$SRC/2335984-Daniel/frontend/quizzstyle.css" "$DST/"
cp "$SRC/2335984-Daniel/frontend/quiz-page.css" "$DST/"
cp "$SRC/shared/css/enhancements.css"           "$DST/css/"

# ---- JavaScript ----
# Student-ID subfolders kept (HTML references them at these paths):
cp "$SRC/2419616-Tarun/js/chat.js"          "$DST/js/2419616/"
cp "$SRC/2419616-Tarun/js/communities.js"   "$DST/js/2419616/"
cp "$SRC/2433841-Tala/js/leaderboard.js"    "$DST/js/2433841/"
# Shared / per-page JS at js/ root:
cp "$SRC/2457928-Gursaaj/js/auth.js"        "$DST/js/"
cp "$SRC/2457928-Gursaaj/js/game.js"        "$DST/js/"
cp "$SRC/2457928-Gursaaj/js/login.js"       "$DST/js/"
cp "$SRC/2457928-Gursaaj/js/news.js"        "$DST/js/"
cp "$SRC/2457928-Gursaaj/js/register.js"    "$DST/js/"
cp "$SRC/2457928-Gursaaj/js/profile.js"     "$DST/js/"
cp "$SRC/shared/js/effects.js"              "$DST/js/"
cp "$SRC/shared/js/enhancements.js"         "$DST/js/"
# quizzes.js + quiz-page.js live at public/ root (not inside public/js/) — matches
# how quizzes.html and quiz-page.html reference them:
cp "$SRC/2335984-Daniel/frontend/quizzes.js"   "$DST/"
cp "$SRC/2335984-Daniel/frontend/quiz-page.js" "$DST/"

# ---- Data ----
cp "$SRC/2419616-Tarun/data/communities.json" "$DST/data/2419616/"

# ---- May's pollution map (vanilla HTML + Leaflet, hosted at /may/) ----
# Note: May replaced the earlier React/Vite app with a single static
# map.html using Leaflet. No build step needed.
MAY_SRC="$SRC/2431260-May/Frontend"
if [ -f "$MAY_SRC/map.html" ]; then
  mkdir -p "$DST/may"
  cp "$MAY_SRC/map.html"  "$DST/may/"
  cp "$MAY_SRC/style.css" "$DST/may/"
  echo ">> May's map deployed to $DST/may/ (map.html + style.css)"
else
  echo ">> skipping May's map (no map.html at $MAY_SRC)"
fi

count=$(find "$DST" -type f | wc -l)
echo ">> OK: $count files written to $DST"
echo ">> next:  cd deploy/public && python3 -m http.server 8000"
echo "          (or just run ./serve.sh from the repo root)"
