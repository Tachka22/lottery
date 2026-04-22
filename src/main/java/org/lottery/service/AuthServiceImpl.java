package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.dto.response.AuthResponse;
import org.lottery.model.User;
import org.lottery.model.enums.Role;
import org.lottery.repository.UserRepository;
import org.lottery.util.JwtUtil;
import org.lottery.util.PasswordUtil;

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    @Inject
    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthResponse register(String email, String password) {
        validateEmail(email);
        validatePassword(password);

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setRole(Role.USER);

        User saved = userRepository.save(user);
        String token = JwtUtil.generateToken((long) saved.getId(), saved.getRole().name());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(String email, String password) {
        validateEmail(email);
        validatePassword(password);

        User user = userRepository.findByEmail(email);
        if (user == null || !PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new IllegalArgumentException("Неверный email или пароль");
        }

        String token = JwtUtil.generateToken((long) user.getId(), user.getRole().name());
        return new AuthResponse(token);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Некорректный email");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Пароль должен быть не менее 4 символов");
        }
    }
}
