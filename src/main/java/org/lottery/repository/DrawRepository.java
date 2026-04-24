package org.lottery.model.repository;

import org.lottery.model.Draw;
import java.util.List;

public interface DrawRepository {

    // Получить все активные тиражи
    List<Draw> findActiveDraws();

    // Найти тираж по ID
    Draw findById(int id);
}