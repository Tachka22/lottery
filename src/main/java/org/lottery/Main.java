package org.lottery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.lottery.config.LotteryModule;
import org.lottery.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 8080;

    public static void main(String[] args) {
        //DatabaseConfig.runMigrations();

        //Настройка DI
        var injector = Guice.createInjector(new LotteryModule());
        var router = injector.getInstance(AppRouter.class);

        //Настройка сериализация
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        //Настраиваем и запускаем веб сервер
        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, config.useVirtualThreads));
            config.http.defaultContentType = "application/json";
            config.useVirtualThreads = true;
            config.requestLogger.http((ctx, executionTimeMs) -> {
                logger.info("{} {} - {}ms", ctx.method(), ctx.path(), executionTimeMs);
            });
            config.router.apiBuilder(router::registerRoutes);
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        }).start(PORT);

        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(400).json(new ErrorResponse(400,  e.getMessage()));

        }).exception(IllegalStateException.class, (e, ctx) -> {
            ctx.status(400).json(new ErrorResponse(400,  e.getMessage()));

        }).exception(io.javalin.validation.ValidationException.class, (e, ctx) -> {
            String details = e.getErrors().entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(java.util.stream.Collectors.joining(", "));
            ctx.status(400).json(new ErrorResponse(400, "Ошибка валидации: " + details));

        }).exception(org.lottery.exception.NotFoundException.class, (e, ctx) -> {
            ctx.status(404).json(new ErrorResponse(404, e.getMessage()));

        }).exception(Exception.class, (e, ctx) -> {
            logger.error("Internal Server Error: ", e);
            ctx.status(500).json(new ErrorResponse(500, "Внутренняя ошибка сервера"));
        });

        app.error(404, ctx -> {
            ctx.json(new ErrorResponse(404, "Ресурс не найден"));
        });


        logger.info("Server started at PORT:{}", PORT);
    }
}