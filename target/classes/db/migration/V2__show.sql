CREATE TABLE show (
                      id VARCHAR(36) PRIMARY KEY,
                      id_integration INTEGER NOT NULL UNIQUE,
                      name VARCHAR(255),
                      type VARCHAR(255),
                      language VARCHAR(255),
                      status VARCHAR(255),
                      runtime INTEGER,
                      average_runtime INTEGER,
                      official_site VARCHAR(255),
                      rating DECIMAL(5, 2),
                      summary CLOB,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);