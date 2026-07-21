# Supavisor Transaction Pooler JDBC Compatibility Design

## Context

The production datasource uses the Supabase Supavisor transaction pooler on port `6543`. PostgreSQL JDBC currently promotes repeatedly executed statements to named server-side prepared statements. Supavisor transaction mode can assign a later transaction to a different PostgreSQL session, where the previously named statement does not exist. This produced `prepared statement "S_22" does not exist` and HTTP 500 responses.

## Decision

Keep the current Supavisor transaction-pooling topology and disable pgJDBC named server-side prepared statements for the `dev` profile by setting Hikari data source property `prepareThreshold` to `0`.

The setting will live in `application-dev.yml`, next to the production datasource configuration. An adjacent comment will document that parameter binding remains enabled and that the option exists specifically for Supavisor transaction-mode compatibility.

## Alternatives Considered

- Add `prepareThreshold=0` to the externally managed JDBC URL. This works but hides an application compatibility requirement in Cloud Run configuration.
- Move to the Supavisor session pooler on port `5432`. This supports named prepared statements but changes database-session capacity characteristics and is broader than the required fix.

## Scope

- Add the pgJDBC property and explanatory comment.
- Add an automated configuration regression test proving that the `dev` profile exposes `prepareThreshold=0` through Hikari data source properties.
- Run the focused test and the repository's default `sh gradlew test` verification.

Hikari pool sizing, Cloud Run scaling limits, Supabase pool limits, and exception-to-HTTP-status mapping are intentionally out of scope.

## Expected Result

Hibernate continues to use bound parameters, while pgJDBC no longer creates named statements such as `S_22`. Requests routed through Supavisor transaction mode therefore do not depend on PostgreSQL session-local prepared-statement state.
