package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkuscoffeeshop.domain.CommandType;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Objects;
import java.util.StringJoiner;

@RegisterForReflection
@Entity
public class CoffeeshopCommand extends PanacheEntity {

    CommandType commandType;

    @Lob
    String commandPayload;

    public CoffeeshopCommand() {
    }

    public CoffeeshopCommand(CommandType commandType, String commandPayload) {
        this.commandType = commandType;
        this.commandPayload = commandPayload;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoffeeshopCommand.class.getSimpleName() + "[", "]")
                .add("commandType=" + commandType)
                .add("commandPayload='" + commandPayload + "'")
                .add("id=" + id)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoffeeshopCommand that = (CoffeeshopCommand) o;
        return commandType == that.commandType &&
                Objects.equals(commandPayload, that.commandPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandType, commandPayload);
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getCommandPayload() {
        return commandPayload;
    }

    public void setCommandPayload(String commandPayload) {
        this.commandPayload = commandPayload;
    }
}

