package de.jkueck.bot.database.repository;

import de.jkueck.bot.database.entity.Operation;
import org.springframework.data.repository.CrudRepository;

public interface OperationRepository extends CrudRepository<Operation, Long> {



}
