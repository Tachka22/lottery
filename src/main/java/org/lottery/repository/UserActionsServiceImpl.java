package org.lottery.repository;

import com.google.inject.Inject;
import org.lottery.dto.response.UserActionResponse;
import org.lottery.service.AuditService;

import java.util.List;

public class UserActionsServiceImpl implements UserActionsService {

    private final UserActionRepository userActionRepository;

    @Inject
    public UserActionsServiceImpl(UserActionRepository userActionRepository) {
        this.userActionRepository = userActionRepository;
    }

    @Override
    public List<UserActionResponse> getUserHistory(int userId) {
        var actions = userActionRepository.getAll(userId);
        return actions.stream().map(s -> new UserActionResponse(
                s.getId(),
                s.getActionType(),
                s.getDetails(),
                s.getCreatedAt()))
                .toList();
    }
}
