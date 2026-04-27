package org.lottery.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Date;

public record UserActionResponse(
        int id,
        String actionType,
        JsonNode details,
        Date createdAt
){
}