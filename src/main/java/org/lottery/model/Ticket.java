package org.lottery.model;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private int drawId;
    private int userId;
    private String numbers;
    private Integer bonus;
    private String status;
    private LocalDateTime createdAt;

    public Ticket() {}

    public Ticket(int id, int drawId, int userId, String numbers, Integer bonus, String status, LocalDateTime createdAt) {
        this.id = id;
        this.drawId = drawId;
        this.userId = userId;
        this.numbers = numbers;
        this.bonus = bonus;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Геттеры
    public int getId() { return id; }
    public int getDrawId() { return drawId; }
    public int getUserId() { return userId; }
    public String getNumbers() { return numbers; }
    public Integer getBonus() { return bonus; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setDrawId(int drawId) { this.drawId = drawId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setNumbers(String numbers) { this.numbers = numbers; }
    public void setBonus(Integer bonus) { this.bonus = bonus; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}