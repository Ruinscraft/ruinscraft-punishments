package com.ruinscraft.punishments.messaging;

import com.sun.istack.internal.NotNull;

public interface MessageConsumer {

    default void consume(@NotNull Message message) {
        System.out.println("Recieved message: " + message.messageId);
    }

}
