-- Добавляем тестового администратора и пользователя
-- Пароли: 'admin123' и 'user123' захешированы BCrypt (примеры)
INSERT INTO users (email, username, password, role) VALUES
                                                        ('admin@lottery.com', 'admin', '$2a$10$N.Zu9VuKwRjOZJx5YqX9v.aJqJ3J3J3J3J3J3J3J3J3J3J3J3J3J3', 'ADMIN'),
                                                        ('user@lottery.com', 'user', '$2a$10$N.Zu9VuKwRjOZJx5YqX9v.aJqJ3J3J3J3J3J3J3J3J3J3J3J3J3J3', 'USER')
    ON CONFLICT (email) DO NOTHING;

-- Добавляем тиражи (разных статусов)
INSERT INTO draws (name, lottery_type, status, winning_combination, created_at, finished_at) VALUES
                                                                                                 ('Новогодний тираж', 'CLASSIC', 'FINISHED', '5,12,33,41,50', '2025-12-20 10:00:00', '2025-12-31 23:59:59'),
                                                                                                 ('Весенний розыгрыш', 'CLASSIC', 'ACTIVE', NULL, '2026-03-01 12:00:00', NULL),
                                                                                                 ('Летняя лотерея', 'BONUS_BALL', 'DRAFT', NULL, '2026-04-01 09:00:00', NULL),
                                                                                                 ('Осенний кубок', 'CLASSIC', 'CANCELLED', NULL, '2026-04-10 15:00:00', NULL)
    ON CONFLICT DO NOTHING;

-- Добавляем билеты для существующих тиражей
INSERT INTO tickets (draw_id, user_id, numbers, status, created_at) VALUES
                                                                        (1, 2, '5,12,33,41,50', 'WIN', '2025-12-25 14:00:00'),   -- выигрышный билет
                                                                        (1, 2, '1,2,3,4,5', 'LOSE', '2025-12-26 11:00:00'),      -- проигрышный
                                                                        (2, 2, '7,14,21,35,49', 'PENDING', '2026-03-05 16:30:00'), -- ожидает розыгрыша
                                                                        (2, 2, '10,20,30,40,50', 'PENDING', '2026-03-10 09:00:00')
    ON CONFLICT DO NOTHING;