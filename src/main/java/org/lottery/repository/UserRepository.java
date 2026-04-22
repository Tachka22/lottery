package org.lottery.repository;

import org.lottery.model.User;

public interface UserRepository {
    User save(User user);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
