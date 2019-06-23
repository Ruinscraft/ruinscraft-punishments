package com.ruinscraft.punishments.messaging;

public interface MessageManager {

    MessageConsumer getConsumer();

    MessageDispatcher getDispatcher();

    default void close() {
    }

}
