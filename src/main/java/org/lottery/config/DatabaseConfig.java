package org.lottery.config;

import org.lottery.controller.AuthController;
import org.lottery.controller.DrawController;
import org.lottery.controller.TicketController;
import org.lottery.model.repository.*;
import org.lottery.service.*;
import org.lottery.util.AuthMiddleware;

public class LotteryModule {

    private final UserRepository userRepository;
    private final AuthController authController;
    private final DrawController drawController;
    private final TicketController ticketController;

    public LotteryModule() {
        // Репозитории
        DatabaseConfig dbConfig = new DatabaseConfig(
                System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://postgres:5432/lottery"),
                System.getenv().getOrDefault("DB_USER", "postgres"),
                System.getenv().getOrDefault("DB_PASSWORD", "postgres")
        );

        this.userRepository = new UserRepositoryImpl(dbConfig);
        DrawRepository drawRepository = new DrawRepositoryImpl(dbConfig);
        TicketRepository ticketRepository = new TicketRepositoryImpl(dbConfig);

        // Сервисы
        AuthService authService = new AuthService(userRepository);
        DrawService drawService = new DrawService(drawRepository);
        NumberGeneratorService numberGenerator = new NumberGeneratorService();
        TicketService ticketService = new TicketService(ticketRepository, drawRepository, numberGenerator);

        // Контроллеры
        this.authController = new AuthController(authService);
        this.drawController = new DrawController(drawService);
        this.ticketController = new TicketController(ticketService);
    }

    public UserRepository getUserRepository() { return userRepository; }
    public AuthController getAuthController() { return authController; }
    public DrawController getDrawController() { return drawController; }
    public TicketController getTicketController() { return ticketController; }
}