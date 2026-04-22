package org.lottery.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    // Секретный ключ — вынести в переменную окружения в реальном проекте
    private static final String SECRET = System.getenv().getOrDefault("JWT_SECRET", "dev-secret-key-must-be-long-enough-32chars");
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 часа

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Генерация токена
    public static String generateToken(Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY)
                .compact();
    }

    // Парсинг токена — возвращает Claims со всеми данными
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Удобные методы для остальных разработчиков
    public static Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    public static String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    // Проверка что токен валиден (не истёк, не повреждён)
    public static boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
