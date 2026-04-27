package org.lottery.repository;

import org.lottery.model.UserAction;

import java.util.List;

public interface UserActionRepository {
    List<UserAction> getAll(int userId);
    void save(UserAction userAction);
}
