CREATE TABLE episode (
                         id VARCHAR(36) PRIMARY KEY,
                         id_integration INTEGER UNIQUE,
                         name VARCHAR(255),
                         season INTEGER,
                         number INTEGER,
                         type VARCHAR(100),
                         runtime INTEGER,
                         summary CLOB,
                         airstamp TIMESTAMP,
                         show_id VARCHAR(36),
                         CONSTRAINT fk_episode_show FOREIGN KEY (show_id) REFERENCES show(id)
);