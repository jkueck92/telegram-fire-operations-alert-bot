package de.jkueck.bot.components;

import de.jkueck.bot.AlertMessage;
import de.jkueck.bot.AlertSearchTerm;
import de.jkueck.bot.EmailReceivedEvent;
import de.jkueck.bot.utils.MailUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.Properties;

@Log4j2
@Component
public class MailChecker {

    @Value("${email.account.username}")
    private String username;

    @Value("${email.account.password}")
    private String password;

    private final ApplicationEventPublisher applicationEventPublisher;

    public MailChecker(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(cron = "${email.scan.cron}")
    public void check() {

        Folder folder = null;
        Store store = null;

        try {

            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);

            store = session.getStore("imaps");

            store.connect("imap.gmail.com", this.username, this.password);

            folder = store.getFolder("INBOX");

            folder.open(Folder.READ_WRITE);

            FlagTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), Boolean.FALSE);
            SearchTerm searchTerm = new AlertSearchTerm();

            Message[] unreadMessages = folder.search(new AndTerm(flagTerm, searchTerm));

            log.info("total messages: " + folder.getMessageCount() + ", unread messages: " + folder.getUnreadMessageCount() + ", search term messages: " + unreadMessages.length);

            for (Message message : unreadMessages) {
                this.applicationEventPublisher.publishEvent(new EmailReceivedEvent(this, new AlertMessage(MailUtils.getText(message))));
                message.setFlag(Flags.Flag.SEEN, Boolean.TRUE);
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (folder != null) {
                try {
                    folder.close(true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
