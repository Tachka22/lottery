-- ============================================
-- 1. Типы лотерей
-- ============================================
INSERT INTO lottery_types (name, numbers_count, min_number, max_number, has_bonus, bonus_min, bonus_max, description) VALUES
    ('CLASSIC', 5, 1, 50, FALSE, NULL, NULL, 'Классическая лотерея: угадай 5 чисел из 50'),
    ('MEGA',    6, 1, 60, TRUE,  1, 10,   'Мега-лотерея: 6 основных чисел + бонусный шар 1..10'),
    ('KENO',    10,1, 80, FALSE, NULL, NULL, 'Кено: выбери 10 чисел из 80')
ON CONFLICT (name) DO NOTHING;




ALTER TABLE users ADD COLUMN IF NOT EXISTS token VARCHAR(255) UNIQUE;

INSERT INTO users (email, username, password, role, token) VALUES
    ('admin@lottery.com', 'admin', '$2a$10$8Z2q5q9qLqLqLqLqLqLqLqOqOqOqOqOqOqOqOqOqOqOqO', 'ADMIN', 'admin-token-123'),
    ('user@lottery.com',  'user',  '$2a$10$8Z2q5q9qLqLqLqLqLqLqLqOqOqOqOqOqOqOqOqOqOqOqO', 'USER', 'user-token-123')
ON CONFLICT (email) DO NOTHING;

-- ============================================
-- 3. Тиражи
-- ============================================
INSERT INTO draws (name, lottery_type_name, status, winning_numbers, winning_bonus, created_at, finished_at, description) VALUES
-- Завершённый тираж CLASSIC (есть победитель)
('Новогодний розыгрыш 2025', 'CLASSIC', 'FINISHED', '5,12,33,41,50', NULL,
 '2025-12-20 10:00:00', '2025-12-31 23:59:59', 'Праздничный тираж'),

-- Активный тираж MEGA (можно покупать билеты)
('Весенний мега-тираж', 'MEGA', 'ACTIVE', 'pending', NULL,
 '2026-03-01 12:00:00', NULL, 'Главный приз 10 млн'),

-- Черновик KENO (ещё не запущен)
('Летнее кено', 'KENO', 'DRAFT', 'pending', NULL,
 '2026-04-01 09:00:00', NULL, 'Тестовый тираж кено'),

-- Отменённый тираж CLASSIC
('Осенний кубок', 'CLASSIC', 'CANCELLED', 'pending', NULL,
 '2026-04-10 15:00:00', NULL, 'Отменён из-за ошибки организатора'),

-- Ещё один завершённый CLASSIC (без победителей)
('Зимний тираж', 'CLASSIC', 'FINISHED', '1,2,3,4,5', NULL,
 '2026-01-10 10:00:00', '2026-01-15 20:00:00', 'Никто не выиграл'),

-- Активный CLASSIC
('Счастливый четверг', 'CLASSIC', 'ACTIVE', 'pending', NULL,
 '2026-04-15 08:00:00', NULL, 'Еженедельный розыгрыш')
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 4. Билеты
-- ============================================
-- Предполагаем, что id пользователя user@lottery.com = 2

-- Для тиража #1 (Новогодний, FINISHED)
INSERT INTO tickets (draw_id, user_id, numbers, bonus, status, created_at) VALUES
    (1, 2, '5,12,33,41,50', NULL, 'WIN',  '2025-12-25 14:00:00'),
    (1, 2, '1,2,3,4,5',     NULL, 'LOSE', '2025-12-26 11:00:00')
ON CONFLICT DO NOTHING;

-- Для тиража #2 (Весенний мега-тираж, ACTIVE)
INSERT INTO tickets (draw_id, user_id, numbers, bonus, status, created_at) VALUES
    (2, 2, '7,14,21,35,49,55', 3, 'PENDING', '2026-03-05 16:30:00'),
    (2, 2, '10,20,30,40,50,60', 7, 'PENDING', '2026-03-10 09:00:00')
ON CONFLICT DO NOTHING;

-- Для тиража #4 (Осенний кубок, CANCELLED)
INSERT INTO tickets (draw_id, user_id, numbers, bonus, status, created_at) VALUES
    (4, 2, '2,4,6,8,10', NULL, 'LOSE', '2026-04-10 16:00:00')
ON CONFLICT DO NOTHING;

-- Для тиража #5 (Зимний тираж, FINISHED)
INSERT INTO tickets (draw_id, user_id, numbers, bonus, status, created_at) VALUES
    (5, 2, '10,20,30,40,50', NULL, 'LOSE', '2026-01-11 09:00:00')
ON CONFLICT DO NOTHING;

-- Для тиража #6 (Счастливый четверг, ACTIVE)
INSERT INTO tickets (draw_id, user_id, numbers, bonus, status, created_at) VALUES
    (6, 2, '3,8,15,22,31', NULL, 'PENDING', '2026-04-15 10:00:00')
ON CONFLICT DO NOTHING;

-- ============================================
-- 5. История операций пользователя
-- ============================================
INSERT INTO user_actions (user_id, action_type, details, created_at) VALUES
    (2, 'BUY_TICKET', '{"ticket_id":1, "draw_name":"Новогодний розыгрыш 2025", "numbers":"5,12,33,41,50"}', '2025-12-25 14:00:00'),
    (2, 'BUY_TICKET', '{"ticket_id":2, "draw_name":"Новогодний розыгрыш 2025", "numbers":"1,2,3,4,5"}',     '2025-12-26 11:00:00'),
    (2, 'VIEW_DRAWS', '{"filter":"active"}', '2026-03-01 13:00:00'),
    (2, 'BUY_TICKET', '{"ticket_id":3, "draw_name":"Весенний мега-тираж", "numbers":"7,14,21,35,49,55", "bonus":3}', '2026-03-05 16:30:00'),
    (2, 'BUY_TICKET', '{"ticket_id":4, "draw_name":"Весенний мега-тираж", "numbers":"10,20,30,40,50,60", "bonus":7}', '2026-03-10 09:00:00'),
    (2, 'CHECK_RESULT', '{"ticket_id":1, "result":"WIN"}',  '2026-01-01 12:00:00'),
    (2, 'CHECK_RESULT', '{"ticket_id":2, "result":"LOSE"}', '2026-01-01 12:01:00'),
    (2, 'EXPORT_REPORT', '{"draw_id":1, "format":"csv", "records":2}', '2026-01-02 15:00:00'),
    (1, 'CANCEL_DRAW',  '{"draw_id":4, "reason":"организационная ошибка"}', '2026-04-10 15:30:00'),
    (2, 'BUY_TICKET', '{"ticket_id":6, "draw_name":"Счастливый четверг", "numbers":"3,8,15,22,31"}', '2026-04-15 10:00:00')
ON CONFLICT DO NOTHING;