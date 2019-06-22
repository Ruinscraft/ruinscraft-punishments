package com.ruinscraft.punishments.messaging.redis;

import com.ruinscraft.punishments.messaging.MessageConsumer;
import com.ruinscraft.punishments.messaging.MessageDispatcher;
import com.ruinscraft.punishments.messaging.MessageManager;

public class RedisMessageManager implements MessageManager {

    @Override
    public MessageConsumer getConsumer() {
        return null;
    }

    @Override
    public MessageDispatcher getDispatcher() {
        return null;
    }

}
