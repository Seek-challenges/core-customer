CREATE TABLE customer (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID del customer: 0e8400-e29b-41d4-a716-446655440000',
    first_name VARCHAR(255) NOT NULL COMMENT 'Nombre del customer',
    last_name VARCHAR(255) UNIQUE NOT NULL COMMENT 'Apellido del customer',
    phone VARCHAR(100) NOT NULL UNIQUE COMMENT 'Teléfono del customer',
    age INT NOT NULL COMMENT 'Edad del customer',
    birth_date DATETIME NOT NULL COMMENT 'Fecha de nacimiento del customer'
)
