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
        var lotteryType = body.get("lotteryTypeName");
        var draw = drawService.createDraw(name, lotteryType);
        ctx.status(201).json(draw);
    }

    public void getAllDraws(Context ctx) {
        var draws = drawService.getAllDraws();
        System.out.println("Returning " + draws.size() + " draws");
        ctx.json(draws);
    }

    public void startDraw(Context ctx) {
        int drawId = Integer.parseInt(ctx.pathParam("drawId"));
        var draw = drawService.startDraw(drawId);
        ctx.json(draw);
    }

    public void finishDraw(Context ctx) {
        int drawId = Integer.parseInt(ctx.pathParam("drawId"));
        var draw = drawService.finishDraw(drawId);
        ctx.json(draw);
    }

    public void cancelDraw(Context ctx) {
        int drawId = Integer.parseInt(ctx.pathParam("drawId"));
        var draw = drawService.cancelDraw(drawId);
        ctx.json(draw);
    }
}
