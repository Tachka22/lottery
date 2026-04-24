package org.lottery.dto.response;

public record FinishDrawResponse(
        String winningNumbers,
        Integer winningBonus
) {}
