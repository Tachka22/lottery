package org.lottery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotteryType {
    private String name;
    private int numbersCount;
    private int minNumber;
    private int maxNumber;
    private boolean hasBonus;
    private Integer bonusMin;
    private Integer bonusMax;
    private String description;
}