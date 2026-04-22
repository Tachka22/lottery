package org.lottery.controller;

import org.lottery.model.Draw;
import org.lottery.model.Ticket;
import org.lottery.model.User;
import org.lottery.service.AuthService;
import org.lottery.service.DrawService;
import org.lottery.service.TicketService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TicketController implements HttpHandler {

    private final AuthService authService;
    private final DrawService drawService;
    private final TicketService ticketService;

    public TicketController(AuthService authService, DrawService drawService, TicketService ticketService) {
        this.authService = authService;
        this.drawService = drawService;
        this.ticketService = ticketService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String path = exchange.getRequestURI().getPath();
            if (path.matches("/draws/\\d+/tickets")) {
                handlePurchaseTicket(exchange);
                return;
            }
        }
        exchange.sendResponseHeaders(404, -1);
    }

    private void handlePurchaseTicket(HttpExchange exchange) throws IOException {

        // ----- 1. Аутентификация -----
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        User user = authService.authenticate(token);
        if (user == null) {
            sendError(exchange, 401, "Не авторизован. Требуется Bearer токен");
            return;
        }

        // ----- 2. Получение ID тиража -----
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        int drawId;
        try {
            drawId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            sendError(exchange, 400, "Неверный ID тиража");
            return;
        }

        // ----- 3. Проверка существования тиража -----
        Draw draw = drawService.getDrawById(drawId);
        if (draw == null) {
            sendError(exchange, 404, "Тираж не найден");
            return;
        }

        // ----- 4. Парсинг тела запроса -----
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            body.append(line);
        }

        String numbers;
        Integer bonus = null;

        try {
            String json = body.toString();

            // Парсим поле numbers
            int numbersIdx = json.indexOf("\"numbers\"");
            if (numbersIdx == -1) {
                throw new Exception("Отсутствует поле numbers");
            }

            int colonIdx = json.indexOf(":", numbersIdx);
            int commaOrBracket = json.indexOf(",", colonIdx);
            int endIdx = commaOrBracket == -1 ? json.indexOf("}", colonIdx) : commaOrBracket;
            String numbersStr = json.substring(colonIdx + 1, endIdx).trim();
            numbers = numbersStr.replaceAll("\"", "");


            int bonusIdx = json.indexOf("\"bonus\"");
            if (bonusIdx != -1) {
                int bonusColonIdx = json.indexOf(":", bonusIdx);
                int bonusCommaOrBracket = json.indexOf(",", bonusColonIdx);
                int bonusEndIdx = bonusCommaOrBracket == -1 ? json.indexOf("}", bonusColonIdx) : bonusCommaOrBracket;
                String bonusStr = json.substring(bonusColonIdx + 1, bonusEndIdx).trim();
                bonus = Integer.parseInt(bonusStr);
            }
        } catch (Exception e) {
            sendError(exchange, 400, "Неверный формат запроса. Ожидается: {\"numbers\": \"1,2,3,4,5\", \"bonus\": 3}");
            return;
        }

        // ----- 5. Покупка билета -----
        try {
            Ticket ticket = ticketService.purchaseTicket(draw, user, numbers, bonus);

            StringBuilder response = new StringBuilder();
            response.append("{")
                    .append("\"id\":").append(ticket.getId()).append(",")
                    .append("\"drawId\":").append(ticket.getDrawId()).append(",")
                    .append("\"numbers\":\"").append(ticket.getNumbers()).append("\",")
                    .append("\"status\":\"").append(ticket.getStatus()).append("\"");
            if (ticket.getBonus() != null) {
                response.append(",\"bonus\":").append(ticket.getBonus());
            }
            response.append("}");

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();

        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (IllegalStateException e) {
            sendError(exchange, 409, e.getMessage());
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = String.format("{\"error\": \"%s\"}", message);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}