package org.lottery.service;

import org.lottery.dto.response.TicketListResponse;
import org.lottery.model.Draw;
import org.lottery.model.Ticket;
import java.util.List;

public interface ReportService {
  DrawReportData getDrawReportData(int drawId);
  TicketListResponse getUserTicketsReportData(int userId);
  String formatToCsvDraw(Draw draw, List<Ticket> tickets);
  String formatToCsvTickets(List<Ticket> tickets);

  // DTO для внутреннего использования
  record DrawReportData(Draw draw, List<Ticket> tickets) {}
}