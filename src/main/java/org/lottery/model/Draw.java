package org.lottery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lottery.model.enums.DrawStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Draw {
    private int id;
    private String name;
    private String lotteryTypeName;
    private DrawStatus status;
    private String winningNumbers;
    private Integer winningBonus;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private String description;
}