package de.jkueck.bot.components;

import de.jkueck.bot.EmailReceivedEvent;
import de.jkueck.bot.database.entity.Operation;
import de.jkueck.bot.database.repository.OperationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AlertDatabaseListener implements ApplicationListener<EmailReceivedEvent> {

    private final OperationRepository operationRepository;

    public AlertDatabaseListener(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public void onApplicationEvent(EmailReceivedEvent event) {
        this.operationRepository.save(new Operation(event.getMessage().getCity(), event.getMessage().getAddress(), event.getMessage().getKeyword(), event.getMessage().getMessage(), event.getMessage().getAlertTimestamp()));
    }

}
