package org.lottery.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lottery.model.UserActionEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class AuditService {
    private final BlockingQueue<UserActionEvent> eventQueue = new LinkedBlockingQueue<>();
    private final List<Consumer<UserActionEvent>> listeners = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "audit-worker"));

    @Inject
    public AuditService(Set<Consumer<UserActionEvent>> listenersFromGuice) {
        listenersFromGuice.forEach(this::subscribe);
        executor.submit(this::processQueue);
    }

    public void emit(int userId, String code, Map<String, Object> params) {
        UserActionEvent event = new UserActionEvent(userId, code, params, System.currentTimeMillis());
        eventQueue.offer(event);
    }

    public void subscribe(Consumer<UserActionEvent> listener) {
        listeners.add(listener);
    }

    private void processQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                UserActionEvent event = eventQueue.take();
                for (Consumer<UserActionEvent> listener : listeners) {
                    try {
                        listener.accept(event);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(e.getMessage());
        }
    }
    public void shutdown() {
        executor.shutdownNow();
    }
}
