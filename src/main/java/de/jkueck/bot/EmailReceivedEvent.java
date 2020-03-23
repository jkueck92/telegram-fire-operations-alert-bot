package de.jkueck.bot;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class EmailReceivedEvent extends ApplicationEvent {

    @Getter
    private AlertMessage message;

    public EmailReceivedEvent(Object source, AlertMessage message) {
        super(source);
        this.message = message;
    }

}
