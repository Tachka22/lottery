package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.model.Draw;
import org.lottery.model.enums.DrawStatus;
import org.lottery.repository.DrawRepository;
import java.time.LocalDateTime;
import java.util.List;

public class DrawServiceImpl implements DrawService {
    private final DrawRepository drawRepository;
    private static final String DEFAULT_LOTTERY_TYPE = "CLASSIC";

    @Inject
    public DrawServiceImpl(DrawRepository drawRepository) {
        this.drawRepository = drawRepository;
    }

    @Override
    public Draw createDraw(String name, String lotteryType) {

        var draw = new Draw();
        draw.setName(name);
        draw.setLotteryType(lotteryType != null ? lotteryType : DEFAULT_LOTTERY_TYPE);
        draw.setStatus(DrawStatus.DRAFT);
        draw.setCreatedAt(LocalDateTime.now());

        return drawRepository.save(draw);
    }

    @Override
    public List<Draw> getAllDraws() {
        return drawRepository.findAll();
    }
}