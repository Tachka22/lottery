package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import org.lottery.dto.request.DrawCreateRequest;
import org.lottery.service.DrawService;

import java.util.Map;

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

    private int getValidId(Context ctx) {
        return ctx.pathParamAsClass("drawId", Integer.class)
                .check(id -> id > 0, "ID тиража должен быть положительным числом")
                .get();
    }
}
