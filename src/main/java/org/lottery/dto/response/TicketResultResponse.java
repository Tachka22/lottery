package org.lottery.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.lottery.model.enums.TicketStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResultResponse {
  private TicketStatus status;
  private String winningCombination;
}