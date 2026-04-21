package org.lottery.repository;

import org.lottery.model.Draw;

import java.util.List;
import java.util.Optional;

public interface DrawRepository {
    Draw save(Draw draw);
    List<Draw> findAll();
    Optional<Draw> findById(int id);
    void update(Draw draw);
}
