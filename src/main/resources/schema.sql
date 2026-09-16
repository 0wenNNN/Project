CREATE TABLE IF NOT EXISTS documents (
    id      INTEGER PRIMARY KEY AUTOINCREMENT, -- Handles Spring lifecycle tracking & native rowid sorting
    key     TEXT NOT NULL UNIQUE,              -- Your custom string key
    value   TEXT NOT NULL,                     -- Your content string
    embed   BLOB NOT NULL                      -- Raw binary Float32 vector embeddings
);