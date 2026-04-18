package org.lottery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lottery.model.enums.DrawStatus;

import java.time.LocalDateTime;

@Data                // Генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor   // Пустой конструктор
@AllArgsConstructor  // Конструктор со всеми полями
public class Draw {
    private int id;
    private String name;
    private String lotteryType;
    private DrawStatus status;
    private String winningCombination;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
}