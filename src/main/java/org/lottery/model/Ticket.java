package org.lottery.model;

import java.time.LocalDateTime;

public class Draw {
    private int id;
    private String name;
    private String lotteryTypeName;
    private String status;
    private String winningNumbers;
    private Integer winningBonus;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private String description;

    public Draw() {}

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLotteryTypeName() { return lotteryTypeName; }
    public String getStatus() { return status; }
    public String getWinningNumbers() { return winningNumbers; }
    public Integer getWinningBonus() { return winningBonus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public String getDescription() { return description; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLotteryTypeName(String lotteryTypeName) { this.lotteryTypeName = lotteryTypeName; }
    public void setStatus(String status) { this.status = status; }
    public void setWinningNumbers(String winningNumbers) { this.winningNumbers = winningNumbers; }
    public void setWinningBonus(Integer winningBonus) { this.winningBonus = winningBonus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    public void setDescription(String description) { this.description = description; }
}