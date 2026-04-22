package org.lottery.config;

import org.lottery.controller.AuthController;
import org.lottery.controller.DrawController;
import org.lottery.controller.TicketController;
import org.lottery.model.repository.*;
import org.lottery.service.AuthService;
import org.lottery.service.DrawService;
import org.lottery.service.TicketService;

public class LotteryModule {

    private final DrawController drawController;
    private final TicketController ticketController;
    private final AuthController authController;

    public LotteryModule(DatabaseConfig dbConfig) {
        // Репозитории
        UserRepository userRepository = new UserRepositoryImpl(dbConfig);
        DrawRepository drawRepository = new DrawRepositoryImpl(dbConfig);
        TicketRepository ticketRepository = new TicketRepositoryImpl(dbConfig);

        // Сервисы
        AuthService authService = new AuthService(userRepository);
        DrawService drawService = new DrawService(drawRepository);
        TicketService ticketService = new TicketService(ticketRepository);

        // Контроллеры
        this.drawController = new DrawController(drawService);
        this.ticketController = new TicketController(authService, drawService, ticketService);
        this.authController = new AuthController(authService);
    }

    public DrawController getDrawController() { return drawController; }
    public TicketController getTicketController() { return ticketController; }
    public AuthController getAuthController() { return authController; }
}