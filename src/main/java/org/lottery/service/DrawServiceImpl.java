package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.dto.request.DrawCreateRequest;
import org.lottery.dto.response.FinishDrawResponse;
import org.lottery.exception.NotFoundException;
import org.lottery.model.Draw;
import org.lottery.model.LotteryType;
import org.lottery.model.enums.DrawStatus;
import org.lottery.repository.DrawRepository;
import org.lottery.repository.LotteryTypeRepository;
import org.lottery.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DrawServiceImpl implements DrawService {
    private final DrawRepository drawRepository;
    private final LotteryTypeRepository lotteryTypeRepository;
    private final TicketRepository ticketRepository;

    @Inject
    public DrawServiceImpl(DrawRepository drawRepository,
                           LotteryTypeRepository lotteryTypeRepository,
                           TicketRepository ticketRepository) {
        this.drawRepository = drawRepository;
        this.lotteryTypeRepository = lotteryTypeRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Draw createDraw(DrawCreateRequest request) {
        if (request.lotteryTypeName() == null || request.lotteryTypeName().isBlank()) {
            throw new IllegalArgumentException("Тип лотереи не задан.");
        }

        if (!lotteryTypeRepository.existsByName(request.lotteryTypeName())) {
            throw new IllegalArgumentException("Лотерея " + request.lotteryTypeName() + "' не найдена или не существует.");
        }

        var draw = new Draw();
        draw.setName(request.name());
        draw.setLotteryTypeName(request.lotteryTypeName());
        draw.setStatus(DrawStatus.DRAFT);
        draw.setCreatedAt(LocalDateTime.now());
        draw.setDescription(request.description());

        return drawRepository.save(draw);
    }

    @Override
    public List<Draw> getAllDraws() {
        return drawRepository.findAll();
    }

    @Override
    public Draw startDraw(int drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Тираж не найден."));

        if (draw.getStatus() != DrawStatus.DRAFT) {
            throw new IllegalStateException("Тираж уже запущен или завершён.");
        }

        draw.setStatus(DrawStatus.ACTIVE);
        drawRepository.update(draw);

        return draw;
    }

    @Override
    public FinishDrawResponse finishDraw(int drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Тираж не найден."));

        LotteryType type = lotteryTypeRepository.findByName(draw.getLotteryTypeName())
                .orElseThrow(() -> new IllegalStateException("Лотерея не найдена."));

        if (draw.getStatus() != DrawStatus.ACTIVE) {
            throw new IllegalStateException("Только активный тираж может быть завершён.");
        }

        String winningNumbers = generateWinningNumbers(type);
        Integer winningBonus = type.isHasBonus() ? generateBonus(type) : null;

        draw.setStatus(DrawStatus.FINISHED);
        draw.setWinningNumbers(winningNumbers);
        draw.setWinningBonus(winningBonus);
        draw.setFinishedAt(LocalDateTime.now());

        drawRepository.update(draw);
        ticketRepository.markWinners(drawId, winningNumbers,  winningBonus);

        return new FinishDrawResponse(winningNumbers, winningBonus);
    }

    @Override
    public void cancelDraw(int drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found"));

        if (draw.getStatus() == DrawStatus.FINISHED || draw.getStatus() == DrawStatus.CANCELLED) {
            throw new IllegalStateException("Нельзя отменить завершённый или отменённый тираж");
        }

        ticketRepository.cancelTickets(drawId);

        draw.setStatus(DrawStatus.CANCELLED);
        draw.setFinishedAt(LocalDateTime.now());
        drawRepository.update(draw);
    }

    @Override
    public Draw getDraw(int drawId) {
        return drawRepository.findById(drawId)
                .orElseThrow(() -> new NotFoundException("Тираж с ID " + drawId + " не найден"));
    }

    @Override
    public Draw getDrawByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя тиража не может быть пустым");
        }

        return drawRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Тираж с именем " + name + " не найден"));
    }

    private String generateWinningNumbers(LotteryType type) {
        Set<Integer> numbers = new HashSet<>();
        int count = type.getNumbersCount();
        int min = type.getMinNumber();
        int max = type.getMaxNumber();

        while (numbers.size() < count) {
            numbers.add(ThreadLocalRandom.current().nextInt(min, max + 1));
        }

        return numbers.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private int generateBonus(LotteryType type) {
        return ThreadLocalRandom.current().nextInt(type.getBonusMin(), type.getBonusMax() + 1);
    }
}