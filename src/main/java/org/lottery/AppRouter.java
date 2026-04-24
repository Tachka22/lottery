package org.lottery;

import com.google.inject.Inject;
import org.lottery.controller.AuthController;
import org.lottery.controller.DrawController;
import org.lottery.controller.ReportController;
import org.lottery.controller.TicketController;
import org.lottery.util.AuthMiddleware;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AppRouter {
    private final DrawController drawController;
    private final AuthController authController;
    private final TicketController ticketController;
    private final ReportController reportController;

    @Inject
    public AppRouter(DrawController drawController, AuthController authController,
                     TicketController ticketController, ReportController reportController) {
        this.drawController = drawController;
        this.authController = authController;
        this.ticketController = ticketController;
        this.reportController = reportController;
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
    }
}
