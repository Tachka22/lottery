package org.lottery.service;

import org.lottery.model.Draw;
import org.lottery.model.Ticket;
import org.lottery.model.User;
import org.lottery.repository.TicketRepository;

public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Покупка билета
     */
    public Ticket purchaseTicket(Draw draw, User user, String numbers, Integer bonus) {

        // Проверка: тираж должен быть активным
        if (!"ACTIVE".equals(draw.getStatus())) {
            throw new IllegalArgumentException("Тираж не активен. Текущий статус: " + draw.getStatus());
        }

        // Проверка: такая комбинация чисел уже существует
        if (ticketRepository.existsByDrawIdAndNumbers(draw.getId(), numbers)) {
            throw new IllegalStateException("Такая комбинация чисел уже занята в этом тираже");
        }

        // Создаём билет
        Ticket ticket = new Ticket();
        ticket.setDrawId(draw.getId());
        ticket.setUserId(user.getId());
        ticket.setNumbers(numbers);
        ticket.setBonus(bonus);
        ticket.setStatus("PENDING");

        return ticketRepository.save(ticket);
    }
}