package org.lottery.repository;

import org.lottery.model.LotteryType;
import java.util.Optional;

import java.util.List;

public interface LotteryTypeRepository {
    boolean existsByName(String name);
    List<LotteryType> findAll();
    LotteryType save(LotteryType type);
    Optional<LotteryType> findByName(String name);
}
