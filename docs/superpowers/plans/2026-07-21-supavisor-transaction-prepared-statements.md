# Supavisor Transaction Pooler JDBC Compatibility Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prevent pgJDBC named prepared-statement failures when the dev profile connects through Supabase Supavisor transaction mode on port 6543.

**Architecture:** Keep the existing datasource URL and Supavisor topology. Pass `prepareThreshold=0` to pgJDBC through Hikari data-source properties and document why parameter binding remains safe.

**Tech Stack:** Spring Boot 4.0.2, HikariCP, pgJDBC, Gradle

---

### Task 1: Configure pgJDBC compatibility

**Files:**
- Modify: `src/main/resources/application-dev.yml`

- [ ] **Step 1: Record the existing production reproduction**

Use the observed production failure as the reproduction evidence:

```text
org.springframework.orm.jpa.JpaSystemException:
JDBC exception executing SQL [ERROR: prepared statement "S_22" does not exist]
```

The datasource endpoint is `pooler.supabase.com:6543`, which identifies Supavisor transaction mode.

- [ ] **Step 2: Add the minimal configuration and explanatory comment**

```yaml
  datasource:
    driver-class-name: org.postgresql.Driver
    url: ${APP_DB_URL:${url}}
    username: ${APP_DB_USERNAME:${rds_username}}
    password: ${APP_DB_PASSWORD:${rds_password}}
    hikari:
      data-source-properties:
        # Disable session-local named statements for Supavisor transaction mode (port 6543).
        prepareThreshold: 0
```

- [ ] **Step 3: Run the repository default verification**

Run: `sh gradlew test`

Expected: BUILD SUCCESSFUL with no failing tests.

- [ ] **Step 4: Review the diff**

Run: `git diff --check && git diff -- src/main/resources/application-dev.yml`

Expected: no whitespace errors; the runtime diff contains only the datasource compatibility property and its explanatory comment.
