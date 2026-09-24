#!/usr/bin/env bash
#
# Smart Tests onboarding for Gradle + TestNG projects.
#
# Usage:
#   ./onboard.sh
#
# Run this from inside your own Gradle+TestNG project directory. If you run
# it somewhere else, it will offer to use the bundled sample project in
# ./demo-project instead, so you can see the flow work end to end before
# trying it on your own repo.
#
# The script checks everything it needs up front and tells you exactly what
# to install (asking before it installs anything for you), then asks a
# couple of quick questions, then runs the whole integration in one go.

set -uo pipefail

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; BOLD='\033[1m'; NC='\033[0m'
ok()    { echo -e "  ${GREEN}[OK]${NC} $1"; }
warn()  { echo -e "  ${YELLOW}[!]${NC} $1"; }
fail()  { echo -e "  ${RED}[MISSING]${NC} $1"; }
banner(){ echo; echo -e "${BOLD}== $1 ==${NC}"; }

confirm() {
  # confirm "question" -> returns 0 for yes
  read -r -p "$1 [y/N] " reply
  [[ "$reply" =~ ^[Yy]$ ]]
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUNDLED_DEMO_DIR="$SCRIPT_DIR/demo-project"

echo -e "${BOLD}Smart Tests onboarding for Gradle + TestNG${NC}"
echo "This will check your setup, ask a couple of quick questions, then run the full integration."

# ---------------------------------------------------------------------------
banner "Step 1 of 5: Checking prerequisites"
# ---------------------------------------------------------------------------

MISSING=0

if command -v java >/dev/null 2>&1 && java -version >/dev/null 2>&1; then
  ok "Java found: $(java -version 2>&1 | grep -m1 'version')"
else
  fail "Java not found (or found but not actually working -- e.g. macOS ships a 'java' stub with no real JDK behind it)."
  echo "      Why you need it: your project itself needs a JVM to build and run,"
  echo "      and the Smart Tests CLI (below) also needs one internally."
  echo "      Install with:  brew install openjdk@17   (or your org's standard JDK)"
  MISSING=1
fi

if command -v git >/dev/null 2>&1; then
  ok "git found: $(git --version)"
else
  fail "git not found."
  echo "      Install with:  xcode-select --install"
  MISSING=1
fi

if command -v python3 >/dev/null 2>&1; then
  ok "python3 found: $(python3 --version)"
else
  fail "python3 not found."
  echo "      Why you need it: the Smart Tests CLI is distributed as a Python package"
  echo "      (it's a generic, language-agnostic tool, not tied to your project's language)."
  echo "      Install with:  brew install python3"
  MISSING=1
fi

if [ $MISSING -eq 1 ]; then
  echo
  fail "Please install the missing tool(s) above, then re-run this script."
  exit 1
fi

# ---------------------------------------------------------------------------
banner "Step 2 of 5: Choosing your project"
# ---------------------------------------------------------------------------

if [ -f "build.gradle" ] || [ -f "build.gradle.kts" ]; then
  ok "Found a Gradle project in the current directory ($(pwd)) -- using it."
  PROJECT_DIR="$(pwd)"
  if [ -x "./gradlew" ]; then
    GRADLE_CMD="./gradlew"
  elif command -v gradle >/dev/null 2>&1; then
    GRADLE_CMD="gradle"
  else
    fail "No ./gradlew wrapper here and no system 'gradle' on PATH."
    if confirm "  Install Gradle now via Homebrew?"; then
      brew install gradle || { fail "Gradle install failed."; exit 1; }
      GRADLE_CMD="gradle"
    else
      fail "Gradle is required. Re-run after installing it."
      exit 1
    fi
  fi
else
  warn "No Gradle project found in the current directory."
  if [ ! -f "$BUNDLED_DEMO_DIR/build.gradle" ]; then
    fail "Bundled demo project not found at $BUNDLED_DEMO_DIR either -- cd into your own Gradle project and re-run."
    exit 1
  fi
  if confirm "  Use the bundled demo project instead, so you can see this work end to end?"; then
    PROJECT_DIR="$BUNDLED_DEMO_DIR"
    ok "Using bundled demo project at $PROJECT_DIR"
    if command -v gradle >/dev/null 2>&1; then
      GRADLE_CMD="gradle"
    else
      fail "System 'gradle' not found (this demo repo has no wrapper)."
      if confirm "  Install Gradle now via Homebrew?"; then
        brew install gradle || { fail "Gradle install failed."; exit 1; }
        GRADLE_CMD="gradle"
      else
        exit 1
      fi
    fi
  else
    fail "cd into your Gradle project directory and re-run this script."
    exit 1
  fi
fi
cd "$PROJECT_DIR"

# ---------------------------------------------------------------------------
banner "Step 3 of 5: Smart Tests CLI"
# ---------------------------------------------------------------------------

PINNED_CLI_VERSION="2.11.2"
if command -v smart-tests >/dev/null 2>&1; then
  INSTALLED_VERSION="$(smart-tests --version 2>&1 | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)"
  if [ "$INSTALLED_VERSION" = "$PINNED_CLI_VERSION" ]; then
    ok "Smart Tests CLI already installed: $(smart-tests --version)"
  else
    warn "Smart Tests CLI installed, but version $INSTALLED_VERSION does not match the pinned version $PINNED_CLI_VERSION."
    if confirm "  Reinstall to the pinned version $PINNED_CLI_VERSION now?"; then
      python3 -m pip install --no-cache-dir "smart-tests-cli==$PINNED_CLI_VERSION" 2>/tmp/pip_err.log \
        || python3 -m pip install --user --break-system-packages --no-cache-dir "smart-tests-cli==$PINNED_CLI_VERSION"
      NEW_VERSION="$(smart-tests --version 2>&1 | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)"
      if [ "$NEW_VERSION" = "$PINNED_CLI_VERSION" ]; then
        ok "Now installed: $(smart-tests --version)"
      else
        fail "Reinstall did not result in $PINNED_CLI_VERSION (still $NEW_VERSION). See /tmp/pip_err.log."
      fi
    else
      warn "Continuing with $INSTALLED_VERSION -- behavior may differ from what was validated on $PINNED_CLI_VERSION."
    fi
  fi
else
  warn "Smart Tests CLI not found."
  if confirm "  Install it now (pip install smart-tests-cli==$PINNED_CLI_VERSION)?"; then
    python3 -m pip install --no-cache-dir "smart-tests-cli==$PINNED_CLI_VERSION" 2>/tmp/pip_err.log
    if ! command -v smart-tests >/dev/null 2>&1; then
      warn "Standard install failed (likely a system-managed Python environment)."
      if confirm "  Retry with 'pip install --user --break-system-packages'?"; then
        python3 -m pip install --user --break-system-packages --no-cache-dir "smart-tests-cli==$PINNED_CLI_VERSION"
      fi
    fi
  fi
  if ! command -v smart-tests >/dev/null 2>&1; then
    fail "Smart Tests CLI still not available on PATH. Please install manually and re-run."
    exit 1
  fi
  ok "Installed: $(smart-tests --version)"
fi

if [ -z "${SMART_TESTS_TOKEN:-}" ]; then
  echo
  echo "Your Smart Tests workspace API token is needed (Unify -> Smart Tests -> Settings -> API key)."
  read -r -s -p "  Paste your SMART_TESTS_TOKEN (input hidden): " SMART_TESTS_TOKEN
  echo
  export SMART_TESTS_TOKEN
fi

echo
smart-tests verify || { fail "Could not verify Smart Tests credentials. Check your token and try again."; exit 1; }

# ---------------------------------------------------------------------------
banner "Step 4 of 5: Choose your integration approach"
# ---------------------------------------------------------------------------

echo "  1) Standalone CLI            (recommended -- no build.gradle changes needed)"
echo "  2) launchable-testng plugin  (build-tool-native, needs one build.gradle dependency)"
read -r -p "  Choose 1 or 2 [1]: " APPROACH
APPROACH="${APPROACH:-1}"

read -r -p "  Test suite name for Smart Tests [gradle-testng-suite]: " TEST_SUITE
TEST_SUITE="${TEST_SUITE:-gradle-testng-suite}"

read -r -p "  Target %% of test suite to run (e.g. 50) [100]: " TARGET_PCT
TARGET_PCT="${TARGET_PCT:-100}"

TAG="build-$(date +%s)"

# ---------------------------------------------------------------------------
banner "Step 5 of 5: Running the integration"
# ---------------------------------------------------------------------------

echo "1. Sanity check: plain test run..."
$GRADLE_CMD test --no-daemon >/tmp/sanity.log 2>&1
if [ $? -ne 0 ]; then
  fail "Base project's tests don't pass on their own -- fix this before adding Smart Tests. See /tmp/sanity.log"
  exit 1
fi
ok "Base project's tests pass."

echo "2. Recording build..."
smart-tests record build --build "$TAG" --source . || exit 1

echo "3. Recording test session..."
smart-tests record session --build "$TAG" --test-suite "$TEST_SUITE" > session.txt || exit 1

if [ "$APPROACH" = "1" ]; then
  echo "4. Selecting test subset (standalone CLI)..."
  smart-tests subset gradle --session @session.txt --target "${TARGET_PCT}%" src/test/java > subset.txt || exit 1
  echo "5. Running selected tests..."
  rm -rf build/test-results build/reports
  $GRADLE_CMD test --no-daemon $(cat subset.txt)
else
  echo "4. Selecting test subset (launchable-testng plugin)..."
  smart-tests subset gradle --session @session.txt --target "${TARGET_PCT}%" --bare src/test/java > subset.txt || exit 1
  echo "5. Running selected tests via launchable-testng plugin..."
  rm -rf build/test-results build/reports
  export LAUNCHABLE_SUBSET_FILE_PATH="$PWD/subset.txt"
  $GRADLE_CMD test --no-daemon --rerun-tasks
fi

echo "6. Recording results back to Smart Tests..."
smart-tests record tests gradle --session @session.txt ./build/test-results/test/*.xml

echo
echo -e "${GREEN}${BOLD}Done.${NC} Your Gradle+TestNG project is now integrated with Smart Tests."
echo "View this session in the Unify UI using the link printed above."
