package org.lottery.service;

import org.lottery.dto.response.TicketListResponse;
import org.lottery.dto.response.TicketResultResponse;

public interface TicketService {
  TicketListResponse getUserTicketHistory(int userId, int limit, int offset);
  TicketResultResponse getTicketResult(int ticketId, int requestingUserId);
}