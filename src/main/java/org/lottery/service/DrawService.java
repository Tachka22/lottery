package org.lottery.service;

import org.lottery.dto.request.DrawCreateRequest;
import org.lottery.dto.response.FinishDrawResponse;
import org.lottery.model.Draw;

import java.util.List;
import java.util.Optional;

public interface DrawService {
    Draw createDraw(DrawCreateRequest request);
    List<Draw> getAllDraws();
    Draw startDraw(int drawId);
    FinishDrawResponse finishDraw(int drawId);
    void cancelDraw(int drawId);
    Draw getDraw(int drawId);
    Draw getDrawByName(String name);
}
