# GitHub Actions — CI

## What is CI

**CI (Continuous Integration)** — automatically validates code on every push. Builds the project, runs tests, checks formatting. If something breaks — it's visible immediately before it reaches `main`.

**Why it matters:**
- Broken code cannot reach `main`
- Formatting issues are caught automatically — no "I forgot to run spotless"
- Every developer gets instant feedback on their changes
- Required status checks block merge until CI is green

---

## How It Works

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant GitHub
    participant Runner as Ubuntu Runner
    participant PG as PostgreSQL (service)

    Dev->>GitHub: git push feature/auth
    GitHub->>Runner: Spin up clean Ubuntu machine
    Runner->>PG: Start postgres:16-alpine container
    loop pg_isready every 10s
        PG-->>Runner: not ready yet
    end
    PG-->>Runner: healthy ✅
    Runner->>Runner: Checkout code
    Runner->>Runner: Set up Java 21 + restore Maven cache
    Runner->>Runner: Build (mvn clean compile)
    Runner->>PG: Run tests (mvn test)
    PG-->>Runner: Flyway migrations applied, tests run
    Runner->>Runner: Package (mvn package -DskipTests)
    Runner->>Runner: Spotless check (mvn spotless:check)
    Runner-->>GitHub: ✅ all steps green / ❌ step failed
    GitHub-->>Dev: Status check result on PR
```

---

## When CI Runs

| Event | CI runs? |
|---|---|
| Push to `feature/auth` | ✅ |
| Push to `main` (after merge) | ✅ |
| Open PR into `main` | ✅ |
| Close PR without merge | ❌ |

---

## Workflow File — `ci.yml`

```yaml
name: CI

on:
  push:
    branches:
      - '**'
  pull_request:
    branches:
      - main

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    env:
      DB_DRIVER: org.postgresql.Driver
      DB_URL: jdbc:postgresql://localhost:5432/verdora_db
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      JWT_SECRET: test-secret-key-for-ci-at-least-32-chars!!
      GOOGLE_CLIENT_ID: test
      GOOGLE_CLIENT_SECRET: test
      REDIRECT_URL: http://localhost:5173
      SERVER_PORT: 8081

    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: verdora_db
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
        ports:
          - 5432:5432
        options: >-
          --health-cmd "pg_isready -U postgres"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build
        run: mvn clean compile -B

      - name: Run tests
        run: mvn test -B

      - name: Package
        run: mvn package -DskipTests -B

      - name: Spotless check
        run: mvn spotless:check -B
```

---

## Steps Breakdown

### What you see in GitHub Actions UI

```
✅ Set up job              2s
✅ Initialize containers   20s   ← PostgreSQL healthcheck
✅ Checkout code           1s
✅ Set up Java 21          0s    ← restored from cache
✅ Build                   15s   ← mvn clean compile
✅ Run tests               25s   ← mvn test + Flyway + Spring context
✅ Package                 10s   ← mvn package -DskipTests
✅ Spotless check          5s    ← mvn spotless:check
✅ Complete job            0s
```

### Why separate steps matter

```mermaid
flowchart TD
    A["Build\nmvn clean compile"] --> B{"compiles?"}
    B -->|"❌ compile error"| F["Fail fast — no need to run tests"]
    B -->|"✅ ok"| C["Run tests\nmvn test"]
    C --> D{"tests pass?"}
    D -->|"❌ test failure"| G["Fail — show exactly which test failed"]
    D -->|"✅ ok"| E["Package\nmvn package -DskipTests"]
    E --> H["Spotless check\nmvn spotless:check"]
    H --> I{"formatted?"}
    I -->|"❌ not formatted"| J["Fail — developer forgot spotless:apply"]
    I -->|"✅ ok"| K["✅ All green — Merge allowed"]

    style F fill:none,stroke:#854F0B,stroke-width:1px
    style G fill:none,stroke:#854F0B,stroke-width:1px
    style J fill:none,stroke:#854F0B,stroke-width:1px
    style K fill:none,stroke:#1D9E75,stroke-width:1px
```

---

## `env` at Job Level

`env` defined at the **job level** is automatically available to all steps — no need to repeat it in every step. Spring Boot reads these variables instead of `.env` (which doesn't exist on the runner).

`JWT_SECRET` is hardcoded — fine for tests. `GOOGLE_CLIENT_ID: test` — placeholder because OAuth2 is not tested in CI.

---

## Spotless in CI — Why It Matters

```mermaid
flowchart LR
    A["Developer writes code"] --> B{"Ran spotless:apply locally?"}
    B -->|"✅ yes"| C["Push → CI → Spotless check passes ✅"]
    B -->|"❌ forgot"| D["Push → CI → Spotless check fails ❌"]
    D --> E["Developer runs mvn spotless:apply"]
    E --> F["Push again → CI passes ✅"]

    style D fill:none,stroke:#854F0B,stroke-width:1px
    style C fill:none,stroke:#1D9E75,stroke-width:1px
    style F fill:none,stroke:#1D9E75,stroke-width:1px
```

`spotless:check` does **not** modify files — it only checks. If any file is not formatted — the step fails with a clear message.

---

## Branch Protection Integration

```mermaid
flowchart TD
    A["Developer opens PR into main"] --> B["CI triggers"]
    B --> C["Build ✅"] --> D["Run tests ✅"] --> E["Package ✅"] --> F["Spotless check ✅"]
    F --> G["Merge allowed ✅"]
    B -->|"any step fails ❌"| H["Merge blocked"]
    H --> I["Fix and push again"]
    I --> B

    style G fill:none,stroke:#1D9E75,stroke-width:1px
    style H fill:none,stroke:#854F0B,stroke-width:1px
```

---

## File Location

```
Verdora-backend/
├── .github/
│   └── workflows/
│       ├── ci.yml    ← this file
│       └── cd.yml
├── src/
├── Dockerfile
└── pom.xml
```
