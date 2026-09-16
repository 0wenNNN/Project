# basic — Spring Boot 4 + SQLite + sqlite-vec

## Build & Run

```sh
./mvnw spring-boot:run          # dev server
./mvnw test                     # all tests
./mvnw test -Dtest=BasicApplicationTests  # single test
./mvnw clean compile            # quick compile check
```

Maven wrapper ships with repo (3.9.16). No local Maven install needed.

## Stack

- **Spring Boot 4.1.0** + Java 25 — `main()` is package-private (`static void main` not `public static void main`)
- **Virtual threads** enabled (`spring.threads.virtual.enabled: true`)
- **SQLite** via Xerial JDBC + HikariCP — DB lives at `${user.home}/search_engine.db`
- **sqlite-vec** — native extension extracted from classpath to `~/.app_runtime/extensions/` on startup. Supports FLOAT32 384-dim cosine vector search. Platform-specific `.dylib`/`.dll`/`.so` bundled as `.zip` in `src/main/resources/sqlite-vec/`; must be unzipped before running. Init symbol is `sqlite3_vector_init` (not derived from filename).
- **WAL mode** via JDBC URL param `?journal_mode=WAL`

## Schema

`documents` table — `id` (auto), `key` (unique), `value`, `embed` (BLOB FLOAT32[384]).

`schema.sql` runs on every startup (`spring.sql.init.mode: always`).

## Key Config

`src/main/resources/application.yaml` — single source of truth. `spring.datasource.url` includes `user.home` for portability.

## Gotchas

- **sqlite-vec init symbol**: `load_extension(path, 'sqlite3_vector_init')` — explicit entry point required (not auto-derived from filename).
- **Spring Data JDBC + SQLite incompatible**: `DataJdbcRepositoriesAutoConfiguration` excluded via `spring.autoconfigure.exclude` because SQLite has no dialect in Spring Data JDBC.
- **Native lib packaging**: sqlite-vec `.dylib`/`.so`/`.dll` inside `.zip` files. Must unzip to `src/main/resources/sqlite-vec/` before running.

## Tests

Single `@SpringBootTest` context-load test. No integration pre-reqs (SQLite is file-based, auto-created).

## Entrypoints

- `com.kurniadhi.mcp.basic.BasicApplication` — `@SpringBootApplication`
- `com.kurniadhi.mcp.basic.config.DataSourceConfig` — extracts native sqlite-vec lib, wires HikariCP with `enable_load_extension=true`
