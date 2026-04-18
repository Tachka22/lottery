package org.lottery.repository;

import org.lottery.model.Draw;

import java.util.List;

public interface DrawRepository {
    Draw save(Draw draw);
    List<Draw> findAll();
}
