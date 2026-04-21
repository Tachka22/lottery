package org.lottery.service;

import org.lottery.model.Draw;

import java.util.List;

public interface DrawService {
    Draw createDraw(String name, String lotteryType);
    List<Draw> getAllDraws();
    Draw startDraw(int drawId);
    Draw finishDraw(int drawId);
    Draw cancelDraw(int drawId);
}
