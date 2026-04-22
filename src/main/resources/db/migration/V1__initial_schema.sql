CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    token VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lottery_types (
    name VARCHAR(30) PRIMARY KEY,
    numbers_count INT NOT NULL,
    min_number INT NOT NULL,
    max_number INT NOT NULL,
    has_bonus BOOLEAN DEFAULT FALSE,
    bonus_min INT,
    bonus_max INT,
    description TEXT
);

CREATE TABLE IF NOT EXISTS draws (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lottery_type_name VARCHAR(30) NOT NULL REFERENCES lottery_types(name),
    status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'ACTIVE', 'FINISHED', 'CANCELLED')),
    winning_numbers VARCHAR(100),
    winning_bonus INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,
    description TEXT
);

CREATE TABLE IF NOT EXISTS tickets (
    id SERIAL PRIMARY KEY,
    draw_id INT NOT NULL REFERENCES draws(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    numbers VARCHAR(100) NOT NULL,
    bonus INT,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'WIN', 'LOSE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_actions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    action_type VARCHAR(50) NOT NULL,
    details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);