package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import org.lottery.dto.response.ErrorResponse;
import org.lottery.model.Draw;
import org.lottery.model.Ticket;
import org.lottery.service.ReportService;
import org.lottery.service.ReportService.DrawReportData;
import org.lottery.dto.response.TicketListResponse;
import org.lottery.util.AuthMiddleware;

public class ReportController {
  private final ReportService reportService;

  @Inject
  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }

  public void getDrawReport(Context ctx) {
    int drawId = parsePathInt(ctx, "drawId");
    if (ctx.result() != null) return; // Если ошибка уже записана

    String format = ctx.queryParamAsClass("format", String.class).getOrDefault("json").toLowerCase();
    DrawReportData data = reportService.getDrawReportData(drawId);

    if ("csv".equals(format)) {
      ctx.contentType("text/csv")
          .result(reportService.formatToCsvDraw(data.draw(), data.tickets()));
    } else {
      ctx.json(DrawReportResponse.of(data.draw(), data.tickets()));
    }
  }

  public void getUserTicketsReport(Context ctx) {
    long userIdLong = AuthMiddleware.getCurrentUserId(ctx);
    String format = ctx.queryParamAsClass("format", String.class).getOrDefault("json").toLowerCase();

    TicketListResponse data = reportService.getUserTicketsReportData((int) userIdLong);

    if ("csv".equals(format)) {
      ctx.contentType("text/csv")
          .result(reportService.formatToCsvTickets(data.getTickets()));
    } else {
      ctx.json(data);
    }
  }

  private int parsePathInt(Context ctx, String paramName) {
    try {
      return Integer.parseInt(ctx.pathParam(paramName));
    } catch (NumberFormatException e) {
      ctx.status(400).json(new ErrorResponse(400, "Неверный формат параметра: " + paramName));
      return -1;
    }
  }

  // DTO для ответа в виде отчёта
  public record DrawReportResponse(Draw draw, java.util.List<Ticket> tickets) {
    public static DrawReportResponse of(Draw d, java.util.List<Ticket> t) {
      return new DrawReportResponse(d, t);
    }
  }
}