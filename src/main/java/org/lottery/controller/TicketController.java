package org.lottery.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import org.lottery.dto.response.ErrorResponse;
import org.lottery.service.TicketService;
import org.lottery.util.AuthMiddleware;

public class TicketController {
  private final TicketService ticketService;

  @Inject
  public TicketController(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  public void getHistory(Context ctx) {
    long userIdLong = AuthMiddleware.getCurrentUserId(ctx);
    int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(20);
    int offset = ctx.queryParamAsClass("offset", Integer.class).getOrDefault(0);

    ctx.json(ticketService.getUserTicketHistory((int) userIdLong, limit, offset));
  }

  public void getResult(Context ctx) {
    long userIdLong = AuthMiddleware.getCurrentUserId(ctx);
    int ticketId;
    try {
      ticketId = Integer.parseInt(ctx.pathParam("ticketId"));
    } catch (NumberFormatException e) {
      ctx.status(400).json(ErrorResponse.of(400, "Неверный формат ticketId"));
      return;
    }
    ctx.json(ticketService.getTicketResult(ticketId, (int) userIdLong));
  }
}