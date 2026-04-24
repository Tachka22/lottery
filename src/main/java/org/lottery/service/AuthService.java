package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.model.User;
import org.lottery.model.repository.UserRepository;

import java.util.UUID;

public class AuthService {

    private final UserRepository userRepository;

    @Inject
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(String email, String password) {
        // Проверка существования пользователя
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Пользователь уже существует");
        }

        // Создание нового пользователя
        String token = UUID.randomUUID().toString();
        User user = new User();
        user.setEmail(email);
        user.setPassword(password); // TODO: хэшировать пароль
        user.setToken(token);
        user.setRole("USER");

        userRepository.save(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null || !password.equals(user.getPassword())) {
            throw new RuntimeException("Неверный email или пароль");
        }

        // Генерация нового токена
        String newToken = UUID.randomUUID().toString();
        userRepository.updateToken(user.getId(), newToken);

        return new AuthResponse(newToken);
    }
}