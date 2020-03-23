package de.jkueck.bot;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;

public class AlertSearchTerm extends SearchTerm {

    @Override
    public boolean match(Message message) {
        try {
            if (message.getSubject().equals("Alarmierung: Einsatz")) {
                return Boolean.TRUE;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

}
