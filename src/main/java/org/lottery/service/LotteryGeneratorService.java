package org.lottery.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lottery.model.LotteryType;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Singleton
public class LotteryGeneratorService {

    @Inject
    public LotteryGeneratorService(){

    }
    public LotteryCombination generateCombination(LotteryType type) {
        Random random = new Random();
        Set<Integer> numbers = new TreeSet<>();

        while (numbers.size() < type.getNumbersCount()) {
            int num = random.nextInt((type.getMaxNumber() - type.getMinNumber()) + 1) + type.getMinNumber();
            numbers.add(num);
        }

        Integer bonus = null;
        if (type.isHasBonus() && type.getBonusMin() != null && type.getBonusMax() != null) {
            bonus = random.nextInt((type.getBonusMax() - type.getBonusMin()) + 1) + type.getBonusMin();
        }

        String numbersString = numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return new LotteryCombination(numbersString, bonus);
    }

    public record LotteryCombination(String numbers, Integer bonus) {}
}
