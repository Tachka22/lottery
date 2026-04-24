package org.lottery.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NumberGeneratorService {

    private final Random random = new Random();

    public String generateNumbers(String lotteryType) {
        switch (lotteryType) {
            case "CLASSIC":
                return generateClassicNumbers();
            case "MEGA":
                return generateMegaNumbers();
            case "KENO":
                return generateKenoNumbers();
            default:
                throw new IllegalArgumentException("Неизвестный тип лотереи: " + lotteryType);
        }
    }

    public int generateBonus() {
        return random.nextInt(10) + 1;
    }

    private String generateClassicNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 50; i++) numbers.add(i);
        Collections.shuffle(numbers);
        List<Integer> selected = numbers.subList(0, 5);
        Collections.sort(selected);
        return selected.toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    private String generateMegaNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 60; i++) numbers.add(i);
        Collections.shuffle(numbers);
        List<Integer> selected = numbers.subList(0, 6);
        Collections.sort(selected);
        return selected.toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    private String generateKenoNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 80; i++) numbers.add(i);
        Collections.shuffle(numbers);
        List<Integer> selected = numbers.subList(0, 10);
        Collections.sort(selected);
        return selected.toString().replace("[", "").replace("]", "").replace(" ", "");
    }
}