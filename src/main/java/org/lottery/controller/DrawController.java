package org.lottery.controller;

import org.lottery.model.Draw;
import org.lottery.service.DrawService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DrawController implements HttpHandler {

    private final DrawService drawService;

    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod()) &&
                exchange.getRequestURI().getPath().equals("/draws/active")) {

            List<Draw> draws = drawService.getActiveDraws();
            String json = convertDrawsToJson(draws);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private String convertDrawsToJson(List<Draw> draws) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < draws.size(); i++) {
            Draw d = draws.get(i);
            sb.append("{")
                    .append("\"id\":").append(d.getId()).append(",")
                    .append("\"name\":\"").append(escapeJson(d.getName())).append("\",")
                    .append("\"lotteryType\":\"").append(d.getLotteryTypeName()).append("\",")
                    .append("\"status\":\"").append(d.getStatus()).append("\"")
                    .append("}");
            if (i < draws.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}