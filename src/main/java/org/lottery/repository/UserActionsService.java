package org.lottery.repository;

import org.lottery.dto.response.UserActionResponse;

import java.util.List;

public interface UserActionsService {
    List<UserActionResponse> getUserHistory(int userId);
}
