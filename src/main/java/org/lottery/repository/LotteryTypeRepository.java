package org.lottery.repository;

public interface LotteryTypeRepository {
    boolean existsByName(String name);
}
