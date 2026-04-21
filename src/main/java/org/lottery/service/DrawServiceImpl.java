package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.model.Draw;
import org.lottery.model.enums.DrawStatus;
import org.lottery.repository.DrawRepository;
import org.lottery.repository.LotteryTypeRepository;

import java.time.LocalDateTime;
import java.util.List;

public class DrawServiceImpl implements DrawService {
    private final DrawRepository drawRepository;
    private final LotteryTypeRepository lotteryTypeRepository;

    @Inject
    public DrawServiceImpl(DrawRepository drawRepository,
                           LotteryTypeRepository lotteryTypeRepository) {
        this.drawRepository = drawRepository;
        this.lotteryTypeRepository = lotteryTypeRepository;
    }

    @Override
    public Draw createDraw(String name, String lotteryType) {
        if (lotteryType == null || lotteryType.isBlank()) {
            throw new IllegalArgumentException("Lottery type is required");
        }

        if (!lotteryTypeRepository.existsByName(lotteryType)) {
            throw new IllegalArgumentException("Lottery type '" + lotteryType + "' does not exist");
        }

        var draw = new Draw();
        draw.setName(name);
        draw.setLotteryTypeName(lotteryType);
        draw.setStatus(DrawStatus.DRAFT);
        draw.setCreatedAt(LocalDateTime.now());
        return drawRepository.save(draw);
    }

    @Override
    public List<Draw> getAllDraws() {
        return drawRepository.findAll();
    }

    @Override
    public Draw startDraw(int drawId) {
        var draw = drawRepository.findById(drawId).orElseThrow(() -> new IllegalArgumentException("Draw not found"));

        if (draw.getStatus() != DrawStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT draw can be started");
        }

        draw.setStatus(DrawStatus.ACTIVE);
        drawRepository.update(draw);

        return draw;
    }

    @Override
    public Draw finishDraw(int drawId) {
        return null;
    }

    @Override
    public Draw cancelDraw(int drawId) {
        return null;
    }
}