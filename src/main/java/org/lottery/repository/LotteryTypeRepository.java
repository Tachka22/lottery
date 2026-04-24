package org.lottery.repository;

import org.lottery.model.LotteryType;

import java.util.Optional;

public interface LotteryTypeRepository {
    boolean existsByName(String name);
    Optional<LotteryType> findByName(String name);
}
