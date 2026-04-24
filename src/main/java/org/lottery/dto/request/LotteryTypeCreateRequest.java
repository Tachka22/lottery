package org.lottery.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LotteryTypeCreateRequest {
  private String name;
  private Integer numbersCount;
  private Integer minNumber;
  private Integer maxNumber;
  private Boolean hasBonus = false;
  private Integer bonusMin;
  private Integer bonusMax;
  private String description;
}