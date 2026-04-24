package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.lottery.dto.request.LotteryTypeCreateRequest;
import org.lottery.model.LotteryType;
import org.lottery.service.LotteryTypeService;

public class LotteryTypeController {
    private final LotteryTypeService service;

    @Inject
    public LotteryTypeController(LotteryTypeService service) {
        this.service = service;
    }

    public void getAll(Context ctx) {
        ctx.json(service.getAllTypes());
    }

    public void create(Context ctx) {
        LotteryTypeCreateRequest request = ctx.bodyValidator(LotteryTypeCreateRequest.class)
                .check(it -> it.getName() != null, "Имя обязательно")
                .get();

        LotteryType type = service.createType(request);
        ctx.status(201).json(type);
    }
}