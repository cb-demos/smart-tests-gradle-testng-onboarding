# Smart Tests — Gradle + TestNG Onboarding Script

A one-command tool that integrates a Gradle + TestNG project with CloudBees Smart Tests. Built to answer a real customer question (no official example previously combined Gradle+TestNG with Smart Tests) and later hardened into something safe to hand to a customer directly.

This README covers everything needed to set it up **from a completely fresh machine** — assume nothing is installed yet.

**Repo contents:**
- `onboard.sh` — the onboarding script
- `README.md` — this file
- `demo-project/` — a small bundled sample Gradle+TestNG project (10 test classes, 58 test methods) the script can use to demo the flow end to end, so you don't need any other repo to try this out

---

## 1. What this script actually does

Smart Tests can look at what code changed and pick a smaller, smart subset of your tests to run instead of the full suite every time. This script wires that up for a Gradle+TestNG project, using one of two integration mechanisms:

- **Approach 1 — Standalone CLI** (recommended, no `build.gradle` changes): the Smart Tests CLI tells Gradle exactly which test classes to run, from outside your project.
- **Approach 2 — `launchable-testng` plugin** (build-tool-native): one dependency added to `build.gradle`; filtering happens via an environment variable instead.

The script checks your prerequisites, asks a couple of quick questions, then runs the whole loop (record build → record session → select subset → run tests → record results) in one go.

---

## 2. Prerequisites — what you need before running it

| Tool | Why it's needed | Check it | Install if missing |
|---|---|---|---|
| **Java 8+** | Your Gradle project needs a JVM to build/run. The Smart Tests CLI *also* needs a JVM internally — an undocumented dependency we found while building this. | `java -version` | `brew install openjdk@17` (macOS) |
| **git** | Used to detect code changes for test selection. | `git --version` | `xcode-select --install` (macOS, ships with Xcode CLI Tools) |
| **python3 + pip** | The Smart Tests CLI is distributed as a Python package — it's a generic, language-agnostic tool, not tied to your project's language. | `python3 --version` | `brew install python3` (macOS) |
| **Gradle** (`./gradlew` or system `gradle`) | Builds and runs your project. Most real Gradle projects already ship a `./gradlew` wrapper — you likely don't need to install anything here. | `./gradlew --version` (run inside your project) | `brew install gradle` (only if your project has no wrapper) |

**The script checks all of this automatically** the moment you run it — you don't need to manually verify each one first. It will tell you exactly what's missing and offer to install it (always asking `[y/N]` first — it never silently changes your machine).

**Platform note:** this script is written for **macOS/Linux (bash)**. It has not been tested on Windows. If you're on Windows, use Git Bash or WSL (Windows Subsystem for Linux), or ask Tejas about a PowerShell equivalent.

---

## 3. Getting your Smart Tests API token

You'll need this before running the script (or you can paste it in when the script asks — input is hidden, never echoed to the screen):

1. Log into the CloudBees Unify console.
2. Go to **Smart Tests → Settings → API key** for your workspace.
3. Copy the token (starts with `v1:...`).

Keep this private — treat it like a password. Don't paste it into Slack, commit it to a repo, or hardcode it into any script.

---

## 4. Getting the script itself

Clone this repo:

```bash
git clone https://github.com/cb-demos/smart-tests-gradle-testng-onboarding.git
cd smart-tests-gradle-testng-onboarding
```

---

## 5. Running it — step by step

```bash
# 1. Make it executable (one-time step)
chmod +x onboard.sh

# 2. Run it
./onboard.sh
```

From here, the script guides you through 5 steps:

**Step 1 of 5 — Prerequisite checks.** Reports `[OK]` or `[MISSING]` for Java/git/python3. If anything's missing, it tells you exactly what to install and stops — install it, then re-run the script.

**Step 2 of 5 — Choosing your project.**
- If you run it **from inside your own Gradle project** (a folder containing `build.gradle`), it uses that project directly.
- If you run it somewhere else, it offers to use the **bundled sample project** in `./demo-project` (10 test classes, 58 test methods, covering a small fake e-commerce domain) instead, so you can see the whole flow work before trying it on a real project. This is part of the same repo — no separate clone needed.

**Step 3 of 5 — Smart Tests CLI.** Installs it via `pip` if not already present, then prompts for your API token (hidden input) and verifies it.

**Step 4 of 5 — Choose your approach.** Prompts for:
- Approach `1` (Standalone CLI, recommended) or `2` (`launchable-testng` plugin)
- A test suite name (press Enter to accept the default)
- A target percentage of tests to run (press Enter for 100%, or enter e.g. `50` to only run half)

**Step 5 of 5 — Runs everything automatically**: sanity check → record build → record session → select subset → run selected tests → record results back to Smart Tests. Ends with a `Done.` message and a link to view results in the Unify UI.

---

## 6. Known limitations / things worth knowing before you test

- **The env var name in the official public docs for Approach 2 only works on newer plugin versions.** Docs say `SMART_TESTS_SUBSET_FILE_PATH` — this was previously unrecognized by the `launchable-testng` plugin and silently ran every test anyway, with zero error or warning. This was a confirmed, tracked issue — [LCHIB-794](https://cloudbees.atlassian.net/browse/LCHIB-794) — and the fix has now shipped starting in `launchable-testng` 1.4.2, with the latest release being [1.4.4](https://central.sonatype.com/artifact/com.launchableinc/launchable-testng/1.4.4/overview) ([smart-tests-testng-plugin#13](https://github.com/cloudbees-oss/smart-tests-testng-plugin/pull/13)). The fix is backward compatible (`LAUNCHABLE_SUBSET_FILE_PATH` still works as a deprecated fallback), which is why this script still uses that legacy name internally — it works on both old and new plugin versions, so you won't hit this either way.
- **`pip install` can fail with `externally-managed-environment`** on modern macOS/Homebrew Python (PEP 668 protection). The script has a fallback (`pip install --user`) if this happens — if that also fails, try `pipx install smart-tests-cli` manually.
- **Selection happens at the test *class* level, not individual test method level.** Smart Tests can include/exclude a whole test class, never a single method inside it — confirmed directly from the CLI's internal API payload (`'type': 'class'`). A test class with many unrelated methods packed into it benefits less from this than several small, focused classes.
- **`--target` and `--confidence` flags don't combine cleanly.** Passing both together silently drops `--confidence` and behaves as if only `--target` were set, with no warning. Only tested on a small test suite so far — worth re-verifying with a larger, more realistic one.
- **Windows is untested** (see Section 2).

---

## 7. Troubleshooting

| Problem | Likely cause / fix |
|---|---|
| `zsh: no such file or directory: ./gradlew` | You're not inside a Gradle project directory, or the project has no committed wrapper. `cd` into your actual project first, or let the script fall back to system `gradle`. |
| `error: externally-managed-environment` during CLI install | Expected on newer macOS Python — the script retries with `--user` automatically. |
| `smart-tests verify` fails | Your API token is wrong, expired, or pasted with extra whitespace/newline. Re-copy it from Unify and try again. |
| Script exits at "Base project's tests don't pass" | Your project's own tests are broken *before* Smart Tests gets involved — fix that first (check `/tmp/sanity.log` for details), it's unrelated to this integration. |
| Nothing prints when typing the API token | Expected — token input is hidden on purpose (like a password prompt). Just paste and press Enter. |

---

## 8. Questions / feedback

This script and its known issues came out of real hands-on testing (not just reading docs) for a real customer question about combining Gradle and TestNG with Smart Tests. If you find something that doesn't match this README, or a new bug, please open an issue — several real product/doc gaps were found this way already and are being routed to the Smart Tests team.
