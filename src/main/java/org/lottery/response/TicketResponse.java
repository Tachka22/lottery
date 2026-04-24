package org.lottery.response;

public class TicketResponse {
    private int id;
    private int drawId;
    private String numbers;
    private Integer bonus;
    private String status;

    public TicketResponse() {}

    public TicketResponse(int id, int drawId, String numbers, Integer bonus, String status) {
        this.id = id;
        this.drawId = drawId;
        this.numbers = numbers;
        this.bonus = bonus;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getDrawId() { return drawId; }
    public void setDrawId(int drawId) { this.drawId = drawId; }
    public String getNumbers() { return numbers; }
    public void setNumbers(String numbers) { this.numbers = numbers; }
    public Integer getBonus() { return bonus; }
    public void setBonus(Integer bonus) { this.bonus = bonus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}