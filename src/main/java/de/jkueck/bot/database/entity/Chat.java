package de.jkueck.bot.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "chat")
public class Chat extends ABaseEntity {

    @Column(nullable = false, unique = true)
    private long telegramChatId;

    @Column(nullable = false)
    private boolean isAdmin = Boolean.FALSE;

    public Chat(long telegramChatId, boolean isAdmin) {
        this.telegramChatId = telegramChatId;
        this.isAdmin = isAdmin;
    }

    public Chat(long telegramChatId) {
        this(telegramChatId, Boolean.FALSE);
    }
}
