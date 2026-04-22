package org.lottery;

import com.google.inject.Inject;
import org.lottery.controller.AuthController;
import org.lottery.controller.DrawController;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AppRouter {
    private final DrawController drawController;
    private final AuthController authController;

    @Inject
    public AppRouter(DrawController drawController, AuthController authController) {
        this.drawController = drawController;
        this.authController = authController;
    }

    public void registerRoutes() {
        path("/auth", () -> {
            post("/register", authController::register);
            post("/login", authController::login);
            get("/verify", authController::verify);
        });

        path("/draws", () -> {
            post(drawController::createDraw);
            get(drawController::getAllDraws);
            post("/{drawId}/start", drawController::startDraw);
            post("/{drawId}/finish", drawController::finishDraw);
            post("/{drawId}/cancel", drawController::cancelDraw);
        });
    }
}
