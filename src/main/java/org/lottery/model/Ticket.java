package org.lottery.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.lottery.model.enums.TicketStatus;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
  private int id;
  private int drawId;
  private int userId;
  private String numbers;
  private Integer bonus;
  private TicketStatus status;
  private LocalDateTime createdAt;
}