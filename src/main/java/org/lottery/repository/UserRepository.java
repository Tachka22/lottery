package org.lottery.model.repository;

import org.lottery.model.User;

public interface UserRepository {
    // Найти пользователя по токену
    User findByToken(String token);

    // Найти пользователя по email
    User findByEmail(String email);

    // Сохранить нового пользователя
    User save(User user);

    // Обновить токен пользователя
    boolean updateToken(int userId, String token);
}