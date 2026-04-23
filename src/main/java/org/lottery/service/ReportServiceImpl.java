package org.lottery.service;

import com.google.inject.Inject;
import io.javalin.http.NotFoundResponse;
import org.lottery.dto.response.TicketListResponse;
import org.lottery.model.Draw;
import org.lottery.model.Ticket;
import org.lottery.repository.DrawRepository;
import org.lottery.repository.TicketRepository;
import java.util.List;

public class ReportServiceImpl implements ReportService {
  private final DrawRepository drawRepository;
  private final TicketRepository ticketRepository;

  @Inject
  public ReportServiceImpl(DrawRepository drawRepository, TicketRepository ticketRepository) {
    this.drawRepository = drawRepository;
    this.ticketRepository = ticketRepository;
  }

  @Override
  public DrawReportData getDrawReportData(int drawId) {
    Draw draw = drawRepository.findById(drawId)
        .orElseThrow(() -> new NotFoundResponse("Тираж не найден"));
    List<Ticket> tickets = ticketRepository.findAllByDrawId(drawId);
    return new DrawReportData(draw, tickets);
  }

  @Override
  public TicketListResponse getUserTicketsReportData(int userId) {
    // Для отчёта берём все билеты без ограничений
    List<Ticket> tickets = ticketRepository.findAllByUserId(userId, Integer.MAX_VALUE, 0);
    return new TicketListResponse(tickets, tickets.size());
  }

  @Override
  public String formatToCsvDraw(Draw draw, List<Ticket> tickets) {
    StringBuilder sb = new StringBuilder();
    sb.append("draw_id,name,lottery_type,status,winning_numbers,created_at\n");
    sb.append(draw.getId()).append(",")
        .append(escapeCsv(draw.getName())).append(",")
        .append(draw.getLotteryTypeName()).append(",")
        .append(draw.getStatus()).append(",")
        .append(!draw.getWinningNumbers().isBlank() ? escapeCsv(draw.getWinningNumbers()) : escapeCsv("Пока не определены")).append(",")
        .append(draw.getCreatedAt()).append("\n\n");

    sb.append("ticket_id,user_id,numbers,status,created_at\n");
    for (Ticket t : tickets) {
      sb.append(t.getId()).append(",")
          .append(t.getUserId()).append(",")
          .append(escapeCsv(t.getNumbers())).append(",")
          .append(t.getStatus()).append(",")
          .append(t.getCreatedAt()).append("\n");
    }
    return sb.toString();
  }

  @Override
  public String formatToCsvTickets(List<Ticket> tickets) {
    StringBuilder sb = new StringBuilder();
    sb.append("ticket_id,draw_id,numbers,status,created_at\n");
    for (Ticket t : tickets) {
      sb.append(t.getId()).append(",")
          .append(t.getDrawId()).append(",")
          .append(escapeCsv(t.getNumbers())).append(",")
          .append(t.getStatus()).append(",")
          .append(t.getCreatedAt()).append("\n");
    }
    return sb.toString();
  }

  private String escapeCsv(String value) {
    if (value == null) return "";
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }
}