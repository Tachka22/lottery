package org.lottery.util;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

public class AuthMiddleware {
    // Просто проверяет что токен есть и валиден
    public static void requireAuth(Context ctx) {
        String token = extractToken(ctx);
        if (token == null || !JwtUtil.isValid(token)) {
            throw new UnauthorizedResponse("Требуется авторизация");
        }
    }

    // Проверяет что пользователь — админ
    public static void requireAdmin(Context ctx) {
        requireAuth(ctx);
        String token = extractToken(ctx);
        if (!"ADMIN".equals(JwtUtil.getRole(token))) {
            throw new ForbiddenResponse("Доступ запрещён");
        }
    }

    // Достаёт userId из токена
    public static Integer getCurrentUserId(Context ctx) {
        String token = extractToken(ctx);
        if (token == null || !JwtUtil.isValid(token)) {
            throw new UnauthorizedResponse("Токен отсутствует или недействителен");
        }
        return JwtUtil.getUserId(token);
    }

    // Вытаскивает токен из заголовка Authorization: Bearer <token>
    private static String extractToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // Достает роль из токена
    public static String getCurrentRole(Context ctx) {
        String token = extractToken(ctx);
        if (token == null || !JwtUtil.isValid(token)) {
            throw new UnauthorizedResponse("Токен отсутствует или недействителен");
        }
        return JwtUtil.getRole(token);
    }
}
