CREATE TABLE IF NOT EXISTS todo (
    todo_id BIGINT INDENTITY PRIMARY KEY,
    todo_title VARCHAR(30),
    finished BOOLEAN,
    created_at TIMESTAMP
);