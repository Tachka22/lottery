package org.lottery.model;

import java.util.Map;

public record UserActionEvent(
        int userId,
        String actionCode,
        Map<String, Object> params,
        long timestamp
) {}