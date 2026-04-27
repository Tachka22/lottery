package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import org.lottery.dto.response.ErrorResponse;
import org.lottery.repository.UserActionsService;
import org.lottery.util.AuthMiddleware;

public class UserActionController {

    private final UserActionsService historyService;

    @Inject
    public UserActionController(UserActionsService historyService) {
        this.historyService = historyService;
    }

    public void getUserHistory(Context ctx) {
        var userId = ctx.pathParamAsClass("userId", Integer.class)
                .check(id -> id > 0, "ID пользователя должен быть положительным")
                .get();

        var currentUserId = AuthMiddleware.getCurrentUserId(ctx);
        if (currentUserId != userId.longValue()) {
            ctx.status(403).json(new ErrorResponse(403, "Доступ запрещён"));
            return;
        }

        var history = historyService.getUserHistory(userId);
        ctx.json(history);
    }
}
