package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import org.lottery.dto.request.AuthRequest;
import org.lottery.service.AuthService;
import org.lottery.util.AuthMiddleware;

import java.util.Map;

public class AuthController {

    private final AuthService authService;

    @Inject
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void register(Context ctx) {
        AuthRequest request = ctx.bodyAsClass(AuthRequest.class);
        var response = authService.register(request.getEmail(), request.getPassword());
        ctx.status(201).json(response);
    }

    public void login(Context ctx) {
        AuthRequest request = ctx.bodyAsClass(AuthRequest.class);
        var response = authService.login(request.getEmail(), request.getPassword());
        ctx.status(200).json(response);
    }

    public void verify(Context ctx) {
        AuthMiddleware.requireAuth(ctx);
        Integer userId = AuthMiddleware.getCurrentUserId(ctx);
        String role = AuthMiddleware.getCurrentRole(ctx);
        ctx.status(200).json(Map.of(
                "userId", userId,
                "role", role
        ));
    }
}
