package org.lottery.controller;

import org.lottery.model.User;
import org.lottery.service.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Контроллер для аутентификации пользователей
 * Эндпоинты:
 * - POST /auth/register - регистрация
 * - POST /auth/login    - вход
 * - GET  /auth/verify   - проверка токена
 */
public class AuthController implements HttpHandler {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // Маршрутизация запросов
        if (method.equals("POST") && path.equals("/auth/register")) {
            handleRegister(exchange);
        } else if (method.equals("POST") && path.equals("/auth/login")) {
            handleLogin(exchange);
        } else if (method.equals("GET") && path.equals("/auth/verify")) {
            handleVerify(exchange);
        } else {
            // Не найденный эндпоинт
            exchange.sendResponseHeaders(404, -1);
        }
    }



    /**
     * POST /auth/register - регистрация нового пользователя
     * Тело запроса: {"email": "user@example.com", "password": "123456"}
     * Ответ: {"token": "uuid-токен"}
     */
    private void handleRegister(HttpExchange exchange) throws IOException {
        // 1. Читаем тело запроса
        String body = readRequestBody(exchange);
        String email = extractJsonValue(body, "email");
        String password = extractJsonValue(body, "password");

        // 2. Валидация
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\": \"Email и пароль обязательны\"}");
            return;
        }

        // 3. Проверка, существует ли пользователь
        User existingUser = authService.findByEmail(email);
        if (existingUser != null) {
            sendResponse(exchange, 409, "{\"error\": \"Пользователь с таким email уже существует\"}");
            return;
        }

        // 4. Создание нового пользователя
        String token = UUID.randomUUID().toString();
        User newUser = authService.register(email, password, token);

        if (newUser == null) {
            sendResponse(exchange, 500, "{\"error\": \"Ошибка при создании пользователя\"}");
            return;
        }

        // 5. Успешный ответ
        sendResponse(exchange, 201, String.format("{\"token\": \"%s\"}", token));
    }


    /**
     * POST /auth/login - вход пользователя
     * Тело запроса: {"email": "user@example.com", "password": "123456"}
     * Ответ: {"token": "новый-токен"}
     */
    private void handleLogin(HttpExchange exchange) throws IOException {
        // 1. Читаем тело запроса
        String body = readRequestBody(exchange);
        String email = extractJsonValue(body, "email");
        String password = extractJsonValue(body, "password");

        // 2. Валидация
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\": \"Email и пароль обязательны\"}");
            return;
        }

        // 3. Аутентификация
        String token = authService.login(email, password);

        if (token == null) {
            sendResponse(exchange, 401, "{\"error\": \"Неверный email или пароль\"}");
            return;
        }

        // 4. Успешный ответ
        sendResponse(exchange, 200, String.format("{\"token\": \"%s\"}", token));
    }



    /**
     * GET /auth/verify - проверка токена
     * Заголовок: Authorization: Bearer <token>
     * Ответ: {"userId": 1, "email": "user@example.com", "role": "USER"}
     */
    private void handleVerify(HttpExchange exchange) throws IOException {
        // 1. Извлекаем токен из заголовка Authorization
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2. Проверка наличия токена
        if (token == null || token.isEmpty()) {
            sendResponse(exchange, 401, "{\"error\": \"Токен не предоставлен\"}");
            return;
        }

        // 3. Проверка валидности токена
        User user = authService.authenticate(token);

        if (user == null) {
            sendResponse(exchange, 401, "{\"error\": \"Неверный токен\"}");
            return;
        }

        // 4. Успешный ответ с данными пользователя
        sendResponse(exchange, 200, String.format(
                "{\"userId\": %d, \"email\": \"%s\", \"role\": \"%s\"}",
                user.getId(),
                user.getEmail(),
                user.getRole()
        ));
    }



    /**
     * Читает тело HTTP запроса
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }


    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;

        int startQuote = json.indexOf("\"", colonIndex);
        if (startQuote == -1) return null;

        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;

        return json.substring(startQuote + 1, endQuote);
    }

    /**
     * Отправляет JSON ответ клиенту
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}