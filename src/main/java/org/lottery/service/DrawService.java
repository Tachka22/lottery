package org.lottery.service;

import org.lottery.model.Draw;
import org.lottery.model.repository.DrawRepository;

import java.util.List;

public class DrawService {

    private final DrawRepository drawRepository;

    public DrawService(DrawRepository drawRepository) {
        this.drawRepository = drawRepository;
    }

    // Получить все активные тиражи
    public List<Draw> getActiveDraws() {
        return drawRepository.findActiveDraws();
    }

    // Получить тираж по ID
    public Draw getDrawById(int id) {
        return drawRepository.findById(id);
    }
}