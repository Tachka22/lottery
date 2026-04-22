package org.lottery.service;

import com.sun.net.httpserver.HttpServer;
import org.lottery.config.LotteryModule;
import java.net.InetSocketAddress;

public class AppRouter {

    private final HttpServer server;

    public AppRouter(int port, LotteryModule module) throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        // Регистрируем обработчики
        server.createContext("/draws/active", module.getDrawController());
        server.createContext("/draws/", module.getTicketController());

        // Эндпоинты авторизации
        server.createContext("/auth/register", module.getAuthController());
        server.createContext("/auth/login", module.getAuthController());
        server.createContext("/auth/verify", module.getAuthController());

        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен на порту 8080");
        System.out.println("Доступные эндпоинты:");
        System.out.println("  POST /auth/register  - регистрация");
        System.out.println("  POST /auth/login     - вход");
        System.out.println("  GET  /auth/verify    - проверка токена");
        System.out.println("  GET  /draws/active   - активные тиражи");
        System.out.println("  POST /draws/{id}/tickets - покупка билета");
    }

    public void stop() {
        server.stop(0);
    }
}