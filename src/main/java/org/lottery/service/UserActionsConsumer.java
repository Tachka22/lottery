package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.model.UserAction;
import org.lottery.model.Event;

import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lottery.repository.UserActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserActionsConsumer implements Consumer<Event> {
    private static final Logger logger = LoggerFactory.getLogger(UserActionsConsumer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserActionRepository  userActionRepository;
    @Inject
    public UserActionsConsumer(UserActionRepository userActionRepository) {

        this.userActionRepository = userActionRepository;
    }

    @Override
    public void accept(Event event) {
        try {
        UserAction action = new UserAction();
        action.setUserId(event.userId());
        action.setActionType(event.actionCode());

        if (event.params() != null) {
            action.setDetails(objectMapper.valueToTree(event.params()));
        }
        userActionRepository.save(action);

        } catch (Exception e) {
            logger.error("Не удалось сохранить аудит в базу данных", e);
        }
    }
}
