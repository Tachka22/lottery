package org.lottery.service;

import com.google.inject.Inject;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import org.lottery.dto.response.TicketListResponse;
import org.lottery.dto.response.TicketResultResponse;
import org.lottery.model.Draw;
import org.lottery.model.LotteryType;
import org.lottery.model.Ticket;
import org.lottery.model.enums.DrawStatus;
import org.lottery.model.enums.TicketStatus;
import org.lottery.repository.DrawRepository;
import org.lottery.repository.LotteryTypeRepository;
import org.lottery.repository.TicketRepository;

import java.util.*;

public class TicketServiceImpl implements TicketService {
  private final TicketRepository ticketRepository;
  private final DrawRepository drawRepository;
  private final LotteryTypeRepository typeRepository;
  private final LotteryGeneratorService generator;

  @Inject
  public TicketServiceImpl(TicketRepository ticketRepository, DrawRepository drawRepository, LotteryTypeRepository typeRepository, LotteryGeneratorService lotteryGeneratorService) {
    this.ticketRepository = ticketRepository;
    this.drawRepository = drawRepository;
    this.typeRepository = typeRepository;
    this.generator = lotteryGeneratorService;
  }

  @Override
  public TicketListResponse getUserTicketHistory(int userId, int limit, int offset) {
    // Валидация пагинации
    if (limit <= 0 || limit > 50) limit = 20;
    if (offset < 0) offset = 0;

    List<Ticket> tickets = ticketRepository.findAllByUserId(userId, limit, offset);
    int total = ticketRepository.countByUserId(userId);
    return new TicketListResponse(tickets, total);
  }

  @Override
  public TicketResultResponse getTicketResult(int ticketId, int requestingUserId) {
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new NotFoundResponse("Билет не найден"));

    if (ticket.getUserId() != requestingUserId) {
      throw new ForbiddenResponse("Доступ запрещён: вы не владелец билета");
    }

    String winningCombo = null;
    if (ticket.getStatus() != TicketStatus.PENDING) {
      Optional<Draw> drawOpt = drawRepository.findById(ticket.getDrawId());
      if (drawOpt.isPresent()) {
        winningCombo = drawOpt.get().getWinningNumbers();
      }
    }

    return new TicketResultResponse(ticket.getStatus(), winningCombo);
  }

  public Ticket buyTicket(int drawId, long userId) {
    Draw draw = drawRepository.findById(drawId)
            .orElseThrow(() -> new IllegalArgumentException("Тираж не найден."));

    if (draw.getStatus() != DrawStatus.ACTIVE) {
      throw new IllegalStateException("Тираж не активен");
    }

    LotteryType type = typeRepository.findByName(draw.getLotteryTypeName())
            .orElseThrow(() -> new IllegalArgumentException("Лотерея не найдена."));

    LotteryGeneratorService.LotteryCombination combination;
    boolean isDuplicate;
    int attempts = 0;
    int maxAttempts = 1000;

    //вероятность совпадения крайне мала, но всё же
    do {
      combination = generator.generateCombination(type);
      isDuplicate = ticketRepository.existsByDrawIdAndNumbers(drawId, combination.numbers());
      attempts++;

      if (attempts > maxAttempts) {
        throw new RuntimeException("Не удалось сгенерировать уникальную комбинацию. Возможно, билеты закончились.");
      }
    } while (isDuplicate);

    Ticket ticket = new Ticket();
    ticket.setDrawId(drawId);
    ticket.setUserId((int) userId);
    ticket.setNumbers(combination.numbers());
    ticket.setBonus(combination.bonus());
    ticket.setStatus(TicketStatus.PENDING);

    return ticketRepository.save(ticket);
  }
}
