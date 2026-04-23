package org.lottery.model;

public record ParticipantNotificationInfo(String email, String status) {
    public boolean isWinner() {
        return "WIN".equalsIgnoreCase(status);
    }
}