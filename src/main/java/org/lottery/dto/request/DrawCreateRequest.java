package org.lottery.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DrawCreateRequest(
        String name,
        @JsonProperty("lotteryTypeName")
        String lotteryTypeName,
        String description
) {}