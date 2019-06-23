package com.ruinscraft.punishments.messaging.redis;

import com.ruinscraft.punishments.Tasks;
import com.ruinscraft.punishments.messaging.MessageConsumer;
import com.ruinscraft.punishments.messaging.MessageDispatcher;
import com.ruinscraft.punishments.messaging.MessageManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisMessageManager implements MessageManager {

    protected static final String REDIS_CHANNEL = "rcpunishments";

    private RedisMessageConsumer consumer;
    private RedisMessageDispatcher dispatcher;

    private JedisPool pool;
    private Jedis subscriber;

    public RedisMessageManager(String host, int port) {
        consumer = new RedisMessageConsumer();
        dispatcher = new RedisMessageDispatcher(this);
        pool = new JedisPool(host, port);
        subscriber = pool.getResource();

        subscriber.connect();

        Tasks.async(() -> subscriber.subscribe(consumer, REDIS_CHANNEL));
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

    protected JedisPool getPool() {
        return pool;
    }

}
