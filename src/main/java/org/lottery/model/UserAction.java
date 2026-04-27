package org.lottery.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAction {
    private Integer id;
    private Integer userId;
    private String actionType;
    private JsonNode details;
    private Date createdAt;
}