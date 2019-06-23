package com.ruinscraft.punishments.messaging;

public interface MessageConsumer {

    default void consume(Message message) {
        System.out.println("Recieved message: " + message.messageId);
    }

}
