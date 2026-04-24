package org.lottery.response;

public class DrawResponse {
    private int id;
    private String name;
    private String lotteryType;
    private String status;

    public DrawResponse() {}

    public DrawResponse(int id, String name, String lotteryType, String status) {
        this.id = id;
        this.name = name;
        this.lotteryType = lotteryType;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLotteryType() { return lotteryType; }
    public void setLotteryType(String lotteryType) { this.lotteryType = lotteryType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}