package org.lottery;

import com.google.inject.Inject;
import org.lottery.controller.*;
import org.lottery.util.AuthMiddleware;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AppRouter {
    private final DrawController drawController;
    private final AuthController authController;
    private final TicketController ticketController;
    private final ReportController reportController;
    private final LotteryTypeController lotteryTypeController;
    private final UserActionController userActionController;

    @Inject
    public AppRouter(DrawController drawController,
                     AuthController authController,
                     TicketController ticketController,
                     ReportController reportController,
                     LotteryTypeController lotteryTypeController,
                     UserActionController userActionController) {
        this.drawController = drawController;
        this.authController = authController;
        this.ticketController = ticketController;
        this.reportController = reportController;
        this.lotteryTypeController = lotteryTypeController;
        this.userActionController = userActionController;
    }

    public void registerRoutes() {
        path("/auth", () -> {
            post("/register", authController::register);
            post("/login", authController::login);
            get("/verify", authController::verify);
        });

        path("/draws", () -> {
            get(ctx -> {
                AuthMiddleware.requireAuth(ctx);
                drawController.getAllDraws(ctx);
            });
            get("/completed", ctx -> {
                AuthMiddleware.requireAuth(ctx);
                drawController.getCompletedDraws(ctx);
            });
            get("/filter", ctx -> {
                AuthMiddleware.requireAuth(ctx);
                drawController.getDrawByFilter(ctx);
            });
            get("/where", ctx -> {
                AuthMiddleware.requireAuth(ctx);
                drawController.getDrawByName(ctx);
            });
            get("/{drawId}", ctx -> {
                AuthMiddleware.requireAuth(ctx);
                drawController.getDraw(ctx);
            });
            post(ctx -> {
                AuthMiddleware.requireAdmin(ctx);
                drawController.createDraw(ctx);
            });
            post("/{drawId}/start", ctx -> {
                AuthMiddleware.requireAdmin(ctx);
                drawController.startDraw(ctx);
            });
            post("/{drawId}/finish", ctx -> {
                AuthMiddleware.requireAdmin(ctx);
                drawController.finishDraw(ctx);
            });
            post("/{drawId}/cancel", ctx -> {
                AuthMiddleware.requireAdmin(ctx);
                drawController.cancelDraw(ctx);
            });
        });

        path("/tickets", () -> {
            get(ctx -> {
              AuthMiddleware.requireAuth(ctx);
              ticketController.getHistory(ctx);
            });
            get("/{ticketId}/result", ctx -> {
              AuthMiddleware.requireAuth(ctx);
              ticketController.getResult(ctx);
            });
        });

        path("/reports", () -> {
            get("/draws/{drawId}", ctx -> {
              AuthMiddleware.requireAdmin(ctx);
              reportController.getDrawReport(ctx);
            });
            get("/tickets", ctx -> {
              AuthMiddleware.requireAuth(ctx);
              reportController.getUserTicketsReport(ctx);
            });
        });

        path("/lottery-types", () -> {
            get(ctx -> {
                AuthMiddleware.requireAuth(ctx);
                lotteryTypeController.getAll(ctx);
            });
            post(ctx -> {
                AuthMiddleware.requireAdmin(ctx);
                lotteryTypeController.create(ctx);
            });
        });

        get("users/{userId}/history", ctx -> {
            AuthMiddleware.requireAuth(ctx);
            userActionController.getUserHistory(ctx);
        });
    }
}
