CREATE TABLE IF NOT EXISTS users (
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(100) UNIQUE NOT NULL,
    username   VARCHAR(50) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS draws (
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    lottery_type        VARCHAR(30) DEFAULT 'CLASSIC',
    status              VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'ACTIVE', 'FINISHED', 'CANCELLED')),
    winning_combination VARCHAR(50),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at         TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS tickets (
    id          SERIAL PRIMARY KEY,
    draw_id     INT NOT NULL REFERENCES draws(id) ON DELETE CASCADE,
    user_id     INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    numbers     VARCHAR(50) NOT NULL,
    status      VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'WIN', 'LOSE')),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_tickets_user_id ON tickets(user_id);
CREATE INDEX IF NOT EXISTS idx_tickets_draw_id ON tickets(draw_id);
CREATE INDEX IF NOT EXISTS idx_draws_status ON draws(status);