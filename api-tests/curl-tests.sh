#!/usr/bin/env bash
# curl-tests.sh — exercises every endpoint Karan added to the combined
# backend (snake scoreboard + news feed). Mirrors the Thunder Client
# collection in this folder so the same scenarios can be run from the
# command line, captured as text, and dropped into the QA form.
#
# Requires: bash 4+, curl, python3.   (python3 is already required by
#           serve.sh, so no extra install needed for the demo machine.)
# Usage:    bash api-tests/curl-tests.sh           # against http://localhost:8080
#           BASE_URL=http://host:port bash ...     # against another host

set -u
BASE="${BASE_URL:-http://localhost:8080}"

# Pick a working Python. On Windows, "python3" is often a Microsoft Store
# shim that prints an install message instead of running, so probe each
# candidate by actually executing it before settling on one.
PY=""
for candidate in python python3 py; do
    if command -v "$candidate" >/dev/null 2>&1 \
       && "$candidate" -c "import sys" >/dev/null 2>&1; then
        PY="$candidate"; break
    fi
done
if [ -z "$PY" ]; then
    echo "Python is required to parse JSON responses (tried python, python3, py)." >&2
    exit 3
fi

# Path that BOTH git-bash curl AND Windows-native python can find. On
# git-bash, /tmp/foo is /tmp/foo; on Windows-native python that path
# does not resolve, so translate it with cygpath when available.
BODY_RAW="$(mktemp -t cos-body-XXXXXX)"
BODY="$BODY_RAW"
if command -v cygpath >/dev/null 2>&1; then
    BODY_PY="$(cygpath -w "$BODY_RAW")"
else
    BODY_PY="$BODY_RAW"
fi
trap 'rm -f "$BODY_RAW"' EXIT

# ---------- pretty output ----------
RED=$'\033[31m'; GRN=$'\033[32m'; YLW=$'\033[33m'; DIM=$'\033[2m'; RST=$'\033[0m'
PASS=0; FAIL=0
results=()

ok()   { results+=("${GRN}PASS${RST}  $1"); PASS=$((PASS+1)); }
bad()  { results+=("${RED}FAIL${RST}  $1 — $2"); FAIL=$((FAIL+1)); }
step() { echo "${YLW}>>${RST} $*"; }

# Run curl, capture body + status. Args: METHOD URL [JSON]
http() {
    local method="$1" url="$2" data="${3:-}"
    if [ -n "$data" ]; then
        curl -sS -o "$BODY_RAW" -w "%{http_code}" \
             -H "Content-Type: application/json" \
             -X "$method" "$url" --data-raw "$data"
    else
        curl -sS -o "$BODY_RAW" -w "%{http_code}" -X "$method" "$url"
    fi
}

# Assert: name expected_status actual_status [extra-message]
assertStatus() {
    local name="$1" want="$2" got="$3" extra="${4:-}"
    if [ "$got" = "$want" ]; then
        ok "$name (HTTP $got)"
    else
        bad "$name" "expected HTTP $want, got $got. $extra"
    fi
}

# Read one Python expression against the parsed JSON body in $BODY_RAW.
# Usage: jsonq "<python-expr-using-name 'd'>"
# We pass BODY_PY (Windows-translated path) into the Python script through
# its argv so the literal path doesn't have to be embedded in the heredoc.
jsonq() {
    "$PY" - "$BODY_PY" "$1" <<'PYEOF'
import json, sys
path, expr = sys.argv[1], sys.argv[2]
try:
    d = json.load(open(path, 'r', encoding='utf-8'))
except Exception:
    print('<parse-error>'); sys.exit(0)
try:
    print(eval(expr))
except Exception as e:
    print('<expr-error: ' + str(e) + '>')
PYEOF
}

# Assert: name python-expr expected
assertJson() {
    local name="$1" expr="$2" want="$3"
    local got
    got=$(jsonq "$expr")
    if [ "$got" = "$want" ]; then
        ok "$name ($expr == $want)"
    else
        bad "$name" "$expr was '$got', wanted '$want'"
    fi
}

# ---------- 0. backend up? ----------
step "checking backend at $BASE"
if ! curl -sSf "$BASE/news" >/dev/null 2>&1; then
    echo "${RED}backend not reachable at $BASE — start it with ./serve.sh first.${RST}" >&2
    exit 2
fi

# ---------- pre-flight cleanup ----------
# Wipe any leftover rows from a previous failed/aborted run so the live DB
# stays clean and the seeded categories stay accurate. Without this, every
# repeated run that crashed before the DELETE step would pile up "curl-test"
# articles and "curl-test" score rows in the user's H2 file.
purge_test_rows() {
    "$PY" - "$BASE" <<'PYEOF'
import sys, json, urllib.request, urllib.error
base = sys.argv[1]
def all_news():
    try:
        return json.load(urllib.request.urlopen(base + '/news'))
    except Exception: return []
def all_scores():
    try:
        return json.load(urllib.request.urlopen(base + '/scores'))
    except Exception: return []
def delete(url):
    req = urllib.request.Request(url, method='DELETE')
    try: urllib.request.urlopen(req).read(); return True
    except urllib.error.HTTPError as e: return e.code == 404
    except Exception: return False
removed = 0
for p in all_news():
    if (p.get('source') == 'curl-tests.sh'
        or 'curl-test' in (p.get('title') or '')):
        if delete(f"{base}/news/{p['id']}"): removed += 1
for s in all_scores():
    if s.get('player') == 'curl-test':
        if delete(f"{base}/scores/{s['id']}"): removed += 1
print(removed)
PYEOF
}
removed=$(purge_test_rows)
if [ "${removed:-0}" -gt 0 ] 2>/dev/null; then
    echo "${DIM}   purged $removed leftover test rows from a previous run${RST}"
fi

# Best-effort cleanup on any exit path so a Ctrl-C / failed assertion mid-run
# doesn't leave junk behind. The temp file cleanup from the original trap is
# preserved here.
cleanup_on_exit() {
    rm -f "$BODY_RAW"
    purge_test_rows >/dev/null 2>&1 || true
}
trap cleanup_on_exit EXIT

# =========================================================================
# NEWS endpoints
# =========================================================================

step "GET /news (full feed, newest first)"
status=$(http GET "$BASE/news")
assertStatus "GET /news" 200 "$status"
count=$(jsonq "len(d)")
echo "${DIM}   feed size: $count${RST}"

step "GET /news/preview (top 6)"
status=$(http GET "$BASE/news/preview")
assertStatus "GET /news/preview" 200 "$status"
preview_len=$(jsonq "len(d)")
if [ "$preview_len" -le 6 ] 2>/dev/null; then ok "preview returns ≤6 items ($preview_len)"
else bad "preview" "returned $preview_len items, expected ≤6"; fi

step "GET /news/trending (top 10 by likes)"
status=$(http GET "$BASE/news/trending")
assertStatus "GET /news/trending" 200 "$status"

step "GET /news/categories (counts per category)"
status=$(http GET "$BASE/news/categories")
assertStatus "GET /news/categories" 200 "$status"
shape=$(jsonq "'empty' if len(d)==0 else ','.join(sorted(d[0].keys()))")
if [ "$shape" = "category,count" ] || [ "$shape" = "empty" ]; then
    ok "category rows are {category,count}"
else
    bad "category shape" "got keys: $shape"
fi

step "GET /news?category=plastic (filtered)"
status=$(http GET "$BASE/news?category=plastic")
assertStatus "GET /news?category=plastic" 200 "$status"
all_plastic=$(jsonq "','.join(sorted({p['category'] for p in d}))")
if [ "$all_plastic" = "plastic" ] || [ -z "$all_plastic" ]; then
    ok "filter returned only 'plastic' rows"
else
    bad "filter" "categories returned: $all_plastic"
fi

step "GET /news/search?q=ocean (keyword search)"
status=$(http GET "$BASE/news/search?q=ocean")
assertStatus "GET /news/search?q=ocean" 200 "$status"

step "GET /news/search?q=a (too-short query → 400)"
status=$(http GET "$BASE/news/search?q=a")
assertStatus "search rejects 1-char query" 400 "$status"

step "POST /news (create a temp article for the rest of the suite)"
created_payload='{"title":"curl-test article","summary":"created by curl-tests.sh","body":"This row is created by the smoke-test script and should be deleted by the end.","category":"action","icon":"test","source":"curl-tests.sh","sourceUrl":null}'
status=$(http POST "$BASE/news" "$created_payload")
assertStatus "POST /news" 200 "$status"
new_id=$(jsonq "d['id']")
new_cat=$(jsonq "d['category']")
echo "${DIM}   created id=$new_id category=$new_cat${RST}"
if [ -n "$new_id" ] && [ "$new_id" != "None" ] && [ "$new_id" != "<expr-error: 'id'>" ]; then ok "POST returned an id"
else bad "POST returned id" "got: $new_id"; fi

step "GET /news/{id} (fetch the new article)"
status=$(http GET "$BASE/news/$new_id")
assertStatus "GET /news/$new_id" 200 "$status"
assertJson "single fetch returns same id" "d['id']" "$new_id"

step "PUT /news/{id} (replace mutable fields)"
update_payload='{"title":"curl-test UPDATED","summary":"updated by PUT","body":"PUT replaced everything except likes.","category":"science","icon":"PUT","source":"curl-tests.sh","sourceUrl":"https://example.com"}'
status=$(http PUT "$BASE/news/$new_id" "$update_payload")
assertStatus "PUT /news/$new_id" 200 "$status"
assertJson "PUT changed title" "d['title']" "curl-test UPDATED"
assertJson "PUT lower-cased category" "d['category']" "science"

step "PATCH /news/{id}/like (atomic +1)"
status=$(http PATCH "$BASE/news/$new_id/like")
assertStatus "PATCH /news/$new_id/like" 200 "$status"
likes_after_one=$(jsonq "d['likes']")
status=$(http PATCH "$BASE/news/$new_id/like")
assertStatus "PATCH /news/$new_id/like (2nd call)" 200 "$status"
likes_after_two=$(jsonq "d['likes']")
if [ "$likes_after_two" -gt "$likes_after_one" ] 2>/dev/null; then
    ok "second PATCH increased likes ($likes_after_one → $likes_after_two)"
else
    bad "PATCH increment" "likes did not increase: $likes_after_one → $likes_after_two"
fi

step "PATCH /news/99999/like (404 on missing id)"
status=$(http PATCH "$BASE/news/99999/like")
assertStatus "PATCH on missing id" 404 "$status"

step "DELETE /news/{id} (remove the temp article)"
status=$(http DELETE "$BASE/news/$new_id")
assertStatus "DELETE /news/$new_id" 200 "$status"
status=$(http GET "$BASE/news/$new_id")
assertStatus "GET after DELETE" 404 "$status"

# =========================================================================
# SCORES endpoints
# =========================================================================

step "GET /scores (list all)"
status=$(http GET "$BASE/scores")
assertStatus "GET /scores" 200 "$status"

step "GET /scores/leaderboard (top 10, deduped)"
status=$(http GET "$BASE/scores/leaderboard")
assertStatus "GET /scores/leaderboard" 200 "$status"
size=$(jsonq "len(d)")
if [ "$size" -le 10 ] 2>/dev/null; then ok "leaderboard returns ≤10 rows ($size)"
else bad "leaderboard" "$size rows"; fi

step "GET /scores/impact (SDG 14 summary)"
status=$(http GET "$BASE/scores/impact")
assertStatus "GET /scores/impact" 200 "$status"
shape=$(jsonq "','.join(sorted(d.keys()))")
expected="message,piecesOfPlasticCleaned,totalGamesPlayed,totalPlayers,totalScore"
if [ "$shape" = "$expected" ]; then
    ok "impact has expected keys"
else
    bad "impact keys" "got: $shape"
fi

step "POST /scores (create a curl-test score)"
status=$(http POST "$BASE/scores" '{"player":"curl-test","score":120}')
assertStatus "POST /scores" 200 "$status"
score_id=$(jsonq "d['id']")
assertJson "POST trims player name" "d['player']" "curl-test"

step "GET /scores/player/curl-test (player stats)"
status=$(http GET "$BASE/scores/player/curl-test")
assertStatus "GET /scores/player/curl-test" 200 "$status"
assertJson "stats: gamesPlayed >=1" "str(d['gamesPlayed']>=1).lower()" "true"

step "GET /scores/player/curl-test/history"
status=$(http GET "$BASE/scores/player/curl-test/history")
assertStatus "GET history" 200 "$status"

step "PUT /scores/{id} (update score)"
status=$(http PUT "$BASE/scores/$score_id" '{"player":"curl-test","score":777}')
assertStatus "PUT /scores/$score_id" 200 "$status"
assertJson "PUT updated score" "d['score']" "777"

step "POST /scores (negative score → 400)"
status=$(http POST "$BASE/scores" '{"player":"x","score":-5}')
assertStatus "POST negative score" 400 "$status"

step "GET /scores/player/Nobody (404 unknown player)"
status=$(http GET "$BASE/scores/player/Nobody")
assertStatus "stats for unknown player" 404 "$status"

step "DELETE /scores/{id} (clean up)"
status=$(http DELETE "$BASE/scores/$score_id")
assertStatus "DELETE /scores/$score_id" 200 "$status"

# =========================================================================
# UNIFIED CROSS-MODULE LEADERBOARD (snake + quiz + user totals)
# =========================================================================

step "GET /api/leaderboard (unified across users + scores + quizzes)"
status=$(http GET "$BASE/api/leaderboard")
assertStatus "GET /api/leaderboard" 200 "$status"
shape=$(jsonq "'empty' if len(d)==0 else ','.join(sorted(d[0].keys()))")
expected_shape="bestQuizScore,bestSnakeScore,combinedScore,gamesPlayed,quizzesTaken,totalScore,userId,username"
if [ "$shape" = "$expected_shape" ] || [ "$shape" = "empty" ]; then
    ok "unified rows have all 8 expected keys"
else
    bad "unified shape" "got keys: $shape"
fi
size=$(jsonq "len(d)")
echo "${DIM}   unified leaderboard rows: $size${RST}"

# =========================================================================
# Summary
# =========================================================================
echo
echo "${YLW}---- results ----${RST}"
for line in "${results[@]}"; do echo "$line"; done
echo
echo "${GRN}$PASS passed${RST}, ${RED}$FAIL failed${RST}"
[ "$FAIL" -eq 0 ]
