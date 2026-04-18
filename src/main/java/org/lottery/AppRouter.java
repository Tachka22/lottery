package org.lottery;

import com.google.inject.Inject;
import org.lottery.controller.DrawController;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AppRouter {
    private final DrawController drawController;

    @Inject
    public AppRouter(DrawController drawController) {
        this.drawController = drawController;
    }

    public void registerRoutes() {
        path("/draws", () -> {
            post(drawController::createDraw);
            get(drawController::getAllDraws);
        });
    }
}
