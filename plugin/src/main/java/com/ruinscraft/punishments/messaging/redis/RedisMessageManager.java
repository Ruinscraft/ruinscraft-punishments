package com.ruinscraft.punishments.messaging.redis;

import com.ruinscraft.punishments.messaging.MessageConsumer;
import com.ruinscraft.punishments.messaging.MessageDispatcher;
import com.ruinscraft.punishments.messaging.MessageManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisMessageManager implements MessageManager {

    protected static final String REDIS_CHANNEL = "rcpunishments";

    private JedisPool pool;
    private Jedis subscriber;

    private RedisMessageConsumer consumer;
    private RedisMessageDispatcher dispatcher;

    public RedisMessageManager(String host, int port) {
        consumer = new RedisMessageConsumer();
        dispatcher = new RedisMessageDispatcher();
    }

    @Override
    public MessageConsumer getConsumer() {
        return consumer;
    }

    @Override
    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public void close() {
        if (consumer.isSubscribed()) {
            consumer.unsubscribe();
        }

        if (!pool.isClosed()) {
            pool.close();
        }

        if (subscriber.isConnected()) {
            subscriber.close();
        }
    }

}
