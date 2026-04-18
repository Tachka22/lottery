package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import org.lottery.service.DrawService;

import java.util.Map;

public class DrawController {
    private final DrawService drawService;

    @Inject
    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    public void createDraw(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        var name = body.get("name");
        var lotteryType = body.get("lotteryType");
        var draw = drawService.createDraw(name, lotteryType);
        ctx.status(201).json(draw);
    }

    public void getAllDraws(Context ctx) {
        var draws = drawService.getAllDraws();
        ctx.json(draws);
    }
}
