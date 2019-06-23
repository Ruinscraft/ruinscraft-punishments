package com.ruinscraft.punishments.messaging.redis;

import com.ruinscraft.punishments.messaging.Message;
import com.ruinscraft.punishments.messaging.MessageDispatcher;
import redis.clients.jedis.Jedis;

public class RedisMessageDispatcher implements MessageDispatcher {

    private RedisMessageManager manager;

    public RedisMessageDispatcher(RedisMessageManager manager) {
        this.manager = manager;
    }

    @Override
    public void dispatch(Message message) {
        try (Jedis jedis = manager.getPool().getResource()) {
            jedis.publish(RedisMessageManager.REDIS_CHANNEL, message.serialize());
        }
    }

}
