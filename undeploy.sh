#!/bin/bash
# undeploy.sh — Linux / macOS / Cloud Shell one-click "take the site offline".
#
# Runs: firebase hosting:disable --site clean-our-sea
#
# Effect: the site stops serving content (visitors get a Firebase
# "site not found" page) but your config, site ID, and deploy history
# are all preserved. To bring it back online, run ./deploy.sh again.
#
# Usage:
#   chmod +x undeploy.sh    # first time only
#   ./undeploy.sh

set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

echo ""
echo "=== firebase hosting:disable --site clean-our-sea ==="
firebase hosting:disable --site clean-our-sea -f

echo ""
echo "=== done ==="
echo "Site clean-our-sea is now offline. Run ./deploy.sh to bring it back."
