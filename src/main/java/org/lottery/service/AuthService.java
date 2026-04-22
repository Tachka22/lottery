package org.lottery.service;

import org.lottery.model.User;
import org.lottery.model.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Проверка токена и получение пользователя
     */
    public User authenticate(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return userRepository.findByToken(token);
    }

    /**
     * Поиск пользователя по email
     */
    public User findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(email);
    }

    /**
     * Регистрация нового пользователя
     */
    public User register(String email, String password, String token) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setToken(token);
        user.setRole("USER");

        return userRepository.save(user);
    }

    /**
     * Вход пользователя
     */
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!password.equals(user.getPassword())) {
            return null;
        }

        // Генерируем новый токен
        String newToken = java.util.UUID.randomUUID().toString();
        userRepository.updateToken(user.getId(), newToken);

        return newToken;
    }
}