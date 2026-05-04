package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import org.lottery.dto.request.DrawCreateRequest;
import org.lottery.model.enums.DrawStatus;
import org.lottery.service.DrawService;

import java.net.URLDecoder;

public class DrawController {
    private final DrawService drawService;

    @Inject
    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    public void createDraw(Context ctx) {
        DrawCreateRequest request = ctx.bodyValidator(DrawCreateRequest.class)
                .check(it -> it.name() != null && !it.name().isBlank(), "Название тиража не может быть пустым")
                .check(it -> it.lotteryTypeName() != null, "Тип лотереи обязателен")
                .get();

        var draw = drawService.createDraw(request);
        ctx.status(201).json(draw);
    }

    public void getAllDraws(Context ctx) {
        var draws = drawService.getAllDraws();
        ctx.json(draws);
    }

    public void startDraw(Context ctx) {
        int id = getValidId(ctx);
        var draw = drawService.startDraw(id);
        ctx.json(draw);
    }

    public void finishDraw(Context ctx) {
        int id = getValidId(ctx);
        var draw = drawService.finishDraw(id);
        ctx.json(draw);
    }

    public void cancelDraw(Context ctx) {
        int id = getValidId(ctx);
        drawService.cancelDraw(id);
        ctx.status(200).json(id);
    }

    public void getDraw(Context ctx) {
        int id = getValidId(ctx);
        var draw = drawService.getDraw(id);
        ctx.status(200).json(draw);
    }

    public void getDrawByName(Context ctx) {
        String name = ctx.queryParam("name");

        if (name == null || name.isBlank()) {
            ctx.status(400).result("Нужно указать имя: ?name=...");
            return;
        }

        String decodedName = URLDecoder.decode(name);
        var draw = drawService.getDrawByName(decodedName);
        ctx.status(200).json(draw);
    }

    private int getValidId(Context ctx) {
        return ctx.pathParamAsClass("drawId", Integer.class)
                .check(id -> id > 0, "ID тиража должен быть положительным числом")
                .get();
    }

    public void getDrawByFilter(Context ctx) {
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(20);
        int offset = ctx.queryParamAsClass("offset", Integer.class).getOrDefault(0);
        String statusParam = ctx.queryParam("status");

        if (statusParam == null || statusParam.isBlank()) {
            ctx.json(drawService.getAllDraws(limit, offset));
            return;
        }

        try {
            DrawStatus status = DrawStatus.valueOf(statusParam.toUpperCase());
            ctx.json(drawService.getDrawsByStatus(status, limit, offset));
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Недопустимый статус: " + statusParam);
        }
    }

    public void getCompletedDraws(Context ctx) {
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(20);
        int offset = ctx.queryParamAsClass("offset", Integer.class).getOrDefault(0);
        ctx.json(drawService.getDrawsByStatus(DrawStatus.FINISHED, limit, offset));
    }
}
