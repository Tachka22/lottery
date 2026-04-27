package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
    private final List<Consumer<Event>> listeners = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "audit-worker"));

    @Inject
    public AuditService(Set<Consumer<Event>> listenersFromGuice) {
        listenersFromGuice.forEach(this::subscribe);
        executor.submit(this::processQueue);
    }

    public void emit(int userId, String code, Map<String, Object> params) {
        if (!eventQueue.offer(new Event(userId, code, params, System.currentTimeMillis()))) {
            logger.warn("Очередь аудита переполнена, событие потеряно: {}", code);
        }
    }

    public void subscribe(Consumer<Event> listener) {
        listeners.add(listener);
    }

    private void processQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Event event = eventQueue.take();
                for (Consumer<Event> listener : listeners) {
                    try {
                        listener.accept(event);
                    } catch (Exception e) {
                        logger.error("Ошибка в подписчике аудита: {}", listener.getClass().getSimpleName(), e);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Поток обработки аудита остановлен");
        }
    }
    public void shutdown() {
        logger.info("Завершение работы AuditService...");
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Поток аудита не завершился вовремя");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
