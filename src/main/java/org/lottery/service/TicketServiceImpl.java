package org.lottery.service;

import com.google.inject.Inject;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import org.lottery.dto.response.TicketListResponse;
import org.lottery.dto.response.TicketResultResponse;
import org.lottery.model.Draw;
import org.lottery.model.Ticket;
import org.lottery.repository.DrawRepository;
import org.lottery.repository.TicketRepository;
import java.util.List;
import java.util.Optional;

public class TicketServiceImpl implements TicketService {
  private final TicketRepository ticketRepository;
  private final DrawRepository drawRepository;

  @Inject
  public TicketServiceImpl(TicketRepository ticketRepository, DrawRepository drawRepository) {
    this.ticketRepository = ticketRepository;
    this.drawRepository = drawRepository;
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
    if (ticket.getStatus() != org.lottery.model.enums.TicketStatus.PENDING) {
      Optional<Draw> drawOpt = drawRepository.findById(ticket.getDrawId());
      if (drawOpt.isPresent()) {
        winningCombo = drawOpt.get().getWinningNumbers();
      }
    }

    return new TicketResultResponse(ticket.getStatus(), winningCombo);
  }
}