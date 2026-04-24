package org.lottery.request;

public class PurchaseTicketRequest {
    private Integer quantity = 1;

    public PurchaseTicketRequest() {}

    public PurchaseTicketRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}