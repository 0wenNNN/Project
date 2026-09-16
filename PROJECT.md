# basic — Project Overview

Spring Boot 4.1.0 web service with SQLite vector search via sqlite-vec.

## Database

| Property | Value |
|----------|-------|
| Engine | SQLite 3 (WAL mode) |
| File | `${user.home}/search_engine.db` | 
| On this machine | `/Users/wcw-e/search_engine.db` |

### Connection

- URL: `jdbc:sqlite:${user.home}/search_engine.db?journal_mode=WAL`
- Pool: HikariCP (max 10, min 2)
- Extension: sqlite-vec (FLOAT32, 384-dim, cosine distance)
- Init: `load_extension(path, 'sqlite3_vector_init')` + `vector_init('documents', 'embed', 'type=FLOAT32,dimension=384,distance=cosine')`

## Schema

### `documents`

| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT |
| key | TEXT | NOT NULL, UNIQUE |
| value | TEXT | NOT NULL |
| embed | BLOB | NOT NULL — FLOAT32[384] |

Created by `schema.sql` on every startup (`spring.sql.init.mode: always`).

## API

| Method | Path | Action |
|--------|------|--------|
| POST | `/api/documents` | Insert a document (key, value) |
| GET | `/api/documents/{key}` | Read document by key |
| PUT | `/api/documents/{key}` | Update document value by key |
| DELETE | `/api/documents/{key}` | Delete document by key |
| GET | `/api/keys` | List all document keys |

## Source Files

| File | Purpose |
|------|---------|
| `BasicApplication.java` | `@SpringBootApplication`, package-private main |
| `DocumentController.java` | `@RestController` with CRUD endpoints |
| `DocumentService.java` | `@Service`, injects `JdbcTemplate`, SQL queries |
| `DocumentRequest.java` | `record` for insert/update request body |
| `DocumentResponse.java` | `record` for response body |
| `DataSourceConfig.java` | Extracts sqlite-vec native lib, wires HikariCP with `enable_load_extension=true` |
| `McpConfig.java` | Registers document tools with MCP server |
| `BasicApplicationTests.java` | `@SpringBootTest` context-load smoke test |

## Config

`src/main/resources/application.yaml` — single source of truth.
