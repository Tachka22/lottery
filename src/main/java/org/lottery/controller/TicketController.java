package org.lottery.controller;

import io.javalin.http.Context;
import org.lottery.request.PurchaseTicketRequest;
import org.lottery.model.Ticket;
import org.lottery.service.TicketService;
import org.lottery.util.AuthMiddleware;

public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void purchaseTicket(Context ctx) {
        AuthMiddleware.requireAuth(ctx);
        Long userId = AuthMiddleware.getCurrentUserId(ctx);
        int drawId = ctx.pathParamAsClass("drawId", Integer.class).get();

        PurchaseTicketRequest request = ctx.bodyAsClass(PurchaseTicketRequest.class);
        if (request == null) {
            request = new PurchaseTicketRequest();
        }

        Ticket ticket = ticketService.purchaseTicket(drawId, userId);

        TicketResponse response = new TicketResponse(
                ticket.getId(),
                ticket.getDrawId(),
                ticket.getNumbers(),
                ticket.getBonus(),
                ticket.getStatus()
        );

        ctx.status(201).json(response);
    }
}