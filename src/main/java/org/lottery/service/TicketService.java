package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.model.Draw;
import org.lottery.model.Ticket;
import org.lottery.model.repository.DrawRepository;
import org.lottery.model.repository.TicketRepository;

public class TicketService {

    private final TicketRepository ticketRepository;
    private final DrawRepository drawRepository;
    private final NumberGeneratorService numberGenerator;

    @Inject
    public TicketService(TicketRepository ticketRepository,
                         DrawRepository drawRepository,
                         NumberGeneratorService numberGenerator) {
        this.ticketRepository = ticketRepository;
        this.drawRepository = drawRepository;
        this.numberGenerator = numberGenerator;
    }

    /**
     * Покупка билета (числа генерируются на сервере)
     */
    public Ticket purchaseTicket(int drawId, long userId) {
        // 1. Получаем тираж
        Draw draw = drawRepository.findById(drawId);
        if (draw == null) {
            throw new IllegalArgumentException("Тираж не найден");
        }

        // 2. Проверяем, активен ли тираж
        if (!"ACTIVE".equals(draw.getStatus())) {
            throw new IllegalStateException("Тираж не активен");
        }

        // 3. Генерируем комбинацию чисел на сервере
        String numbers = numberGenerator.generateNumbers(draw.getLotteryTypeName());

        // 4. Генерируем бонус (для MEGA лотереи)
        Integer bonus = null;
        if ("MEGA".equals(draw.getLotteryTypeName())) {
            bonus = numberGenerator.generateBonus();
        }

        // 5. Создаём билет
        Ticket ticket = new Ticket();
        ticket.setDrawId(drawId);
        ticket.setUserId((int) userId);
        ticket.setNumbers(numbers);
        ticket.setBonus(bonus);
        ticket.setStatus("PENDING");

        // 6. Сохраняем
        return ticketRepository.save(ticket);
    }
}