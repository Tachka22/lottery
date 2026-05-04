package org.lottery.repository;

import org.lottery.model.Draw;
import org.lottery.model.enums.DrawStatus;

import java.util.List;
import java.util.Optional;

public interface DrawRepository {
    Draw save(Draw draw);
    List<Draw> findAll();
    List<Draw> findAll(int limit, int offset);
    Optional<Draw> findById(int id);
    void update(Draw draw);
    Optional<Draw> findByName(String name);
    List<Draw> findByStatus(DrawStatus status, int limit, int offset);
}
