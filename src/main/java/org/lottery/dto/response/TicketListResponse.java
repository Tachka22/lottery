package org.lottery.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.lottery.model.Ticket;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketListResponse {
  private List<Ticket> tickets;
  private int total;
}