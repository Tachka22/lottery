package org.lottery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.lottery.config.DatabaseConfig;
import org.lottery.config.LotteryModule;
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
        Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, config.concurrency.useVirtualThreads));  //Настройка сериализация
            config.http.defaultContentType = "application/json";
            config.concurrency.useVirtualThreads = true;
            config.requestLogger.http((ctx, executionTimeMs) -> {                           //Логирование запросов
                logger.info("{} {} - {}ms", ctx.method(), ctx.path(), executionTimeMs);
            });
            config.routes.apiBuilder(router::registerRoutes);//Настройка обработчиков запросов
        }).start(PORT);

        logger.info("Server started at PORT:{}", PORT);
    }
}