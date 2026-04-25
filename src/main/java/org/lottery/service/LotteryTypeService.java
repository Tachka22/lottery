package org.lottery.service;

import org.lottery.dto.request.LotteryTypeCreateRequest;
import org.lottery.model.LotteryType;
import java.util.List;

public interface LotteryTypeService {
    List<LotteryType> getAllTypes();
    LotteryType createType(LotteryTypeCreateRequest request);
}