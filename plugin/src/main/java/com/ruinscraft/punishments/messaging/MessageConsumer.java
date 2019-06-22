package com.ruinscraft.punishments.messaging;

public interface MessageConsumer {

    default void consume(Message message) {
        if (message == null) {
            return;
        }
    }

}
