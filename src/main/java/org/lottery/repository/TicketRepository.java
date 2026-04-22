package org.lottery.model.repository;

import org.lottery.model.Ticket;

public interface TicketRepository {

    // Сохранить новый билет
    Ticket save(Ticket ticket);

    // Проверить, занята ли комбинация чисел в тираже
    boolean existsByDrawIdAndNumbers(int drawId, String numbers);
}