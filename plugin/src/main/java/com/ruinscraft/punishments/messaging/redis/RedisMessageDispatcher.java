package com.ruinscraft.punishments.messaging.redis;

import com.ruinscraft.punishments.messaging.Message;
import com.ruinscraft.punishments.messaging.MessageDispatcher;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CompletableFuture;

public class RedisMessageDispatcher implements MessageDispatcher {

    private RedisMessageManager manager;

    public RedisMessageDispatcher(RedisMessageManager manager) {
        this.manager = manager;
    }

    @Override
    public CompletableFuture<Void> dispatch(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = manager.getPool().getResource()) {
                jedis.publish(RedisMessageManager.REDIS_CHANNEL, message.serialize());
            }

            return null;
        });
    }

}
