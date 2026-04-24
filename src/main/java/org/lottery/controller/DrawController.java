package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import org.lottery.service.DrawService;

import java.util.List;
import java.util.stream.Collectors;

public class DrawController {

    private final DrawService drawService;

    @Inject
    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    /**
     * GET /draws/active - список активных тиражей
     */
    public void getActiveDraws(Context ctx) {
        List<DrawResponse> draws = drawService.getActiveDraws().stream()
                .map(draw -> new DrawResponse(
                        draw.getId(),
                        draw.getName(),
                        draw.getLotteryTypeName(),
                        draw.getStatus()
                ))
                .collect(Collectors.toList());

        ctx.status(200).json(draws);
    }
}