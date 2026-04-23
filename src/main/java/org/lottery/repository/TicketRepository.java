package org.lottery.repository;

import org.lottery.model.Ticket;
import java.util.List;
import java.util.Optional;

public interface TicketRepository {
  Optional<Ticket> findById(int id);
  List<Ticket> findAllByUserId(int userId, int limit, int offset);
  int countByUserId(int userId);
  List<Ticket> findAllByDrawId(int drawId);
}