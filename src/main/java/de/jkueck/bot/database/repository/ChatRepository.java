package de.jkueck.bot.database.repository;

import de.jkueck.bot.database.entity.Chat;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChatRepository extends CrudRepository<Chat, Long> {

    Optional<Chat> findByTelegramChatId(final Long telegramChatId);

}
