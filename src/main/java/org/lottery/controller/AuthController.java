package org.lottery.controller;

import io.javalin.http.Context;
import org.lottery.request.AuthRequest;
import org.lottery.service.AuthService;
import org.lottery.util.AuthMiddleware;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void register(Context ctx) {
        AuthRequest request = ctx.bodyAsClass(AuthRequest.class);
        AuthResponse response = authService.register(request.getEmail(), request.getPassword());
        ctx.status(201).json(response);
    }

    public void login(Context ctx) {
        AuthRequest request = ctx.bodyAsClass(AuthRequest.class);
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        ctx.status(200).json(response);
    }

    public void verify(Context ctx) {
        AuthMiddleware.requireAuth(ctx);
        Long userId = AuthMiddleware.getCurrentUserId(ctx);
        String role = AuthMiddleware.getCurrentRole(ctx);
        ctx.status(200).json(new VerifyResponse(userId, role));
    }
}