package com.ruinscraft.punishments.messaging.redis;

import com.ruinscraft.punishments.messaging.MessageConsumer;
import com.ruinscraft.punishments.messaging.MessageDispatcher;
import com.ruinscraft.punishments.messaging.MessageManager;
import com.ruinscraft.punishments.util.Tasks;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.CompletableFuture;

public class RedisMessageManager implements MessageManager {

    protected static final String REDIS_CHANNEL = "rcpunishments";

    private final String host;
    private final int port;

    private RedisMessageConsumer consumer;
    private RedisMessageDispatcher dispatcher;

    private JedisPool pool;
    private Jedis subscriber;

    public RedisMessageManager(String host, int port) {
        this.host = host;
        this.port = port;

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
        if (pool == null || pool.isClosed()) {
            pool = new JedisPool(host, port);
        }
        return pool;
    }

}
