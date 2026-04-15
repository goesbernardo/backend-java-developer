CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- seed admin
INSERT INTO users (id, username, password, role, enabled)
VALUES (
           '08b5a3a9-8874-4fd7-b79a-45c877a65f6e',
           'admin',
           '$2a$10$JXZFFAfBRw38kKLQ13Hm0eSn1MzYSpixE8Ble9paV4tHOFgbPQKhW',
           'ADMIN',
           TRUE
       );