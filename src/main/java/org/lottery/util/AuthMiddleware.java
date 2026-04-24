package org.lottery.util;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.lottery.model.User;
import org.lottery.model.repository.UserRepository;

/**
 * Промежуточный слой для проверки аутентификации
 * Используется в контроллерах для защиты эндпоинтов
 */
public class AuthMiddleware {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String USER_ATTRIBUTE = "currentUser";

    private static UserRepository userRepository;

    /**
     * Инициализация (вызвать при старте приложения)
     */
    public static void init(UserRepository repository) {
        userRepository = repository;
    }

    /**
     * Проверка наличия и валидности токена
     */
    public static void requireAuth(Context ctx) {
        String authHeader = ctx.header("Authorization");

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            throw new UnauthorizedResponse("Требуется авторизация. Используйте Bearer токен");
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());

        if (userRepository == null) {
            throw new IllegalStateException("AuthMiddleware не инициализирован");
        }

        User user = userRepository.findByToken(token);

        if (user == null) {
            throw new UnauthorizedResponse("Неверный или просроченный токен");
        }

        // Сохраняем пользователя в контексте для дальнейшего использования
        ctx.attribute(USER_ATTRIBUTE, user);
    }

    /**
     * Получить ID текущего авторизованного пользователя
     */
    public static Long getCurrentUserId(Context ctx) {
        User user = ctx.attribute(USER_ATTRIBUTE);
        if (user == null) {
            throw new UnauthorizedResponse("Пользователь не авторизован");
        }
        return (long) user.getId();
    }

    /**
     * Получить роль текущего авторизованного пользователя
     */
    public static String getCurrentRole(Context ctx) {
        User user = ctx.attribute(USER_ATTRIBUTE);
        if (user == null) {
            throw new UnauthorizedResponse("Пользователь не авторизован");
        }
        return user.getRole();
    }

    /**
     * Получить email текущего авторизованного пользователя
     */
    public static String getCurrentEmail(Context ctx) {
        User user = ctx.attribute(USER_ATTRIBUTE);
        if (user == null) {
            throw new UnauthorizedResponse("Пользователь не авторизован");
        }
        return user.getEmail();
    }
}