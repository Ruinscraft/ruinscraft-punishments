package com.ruinscraft.punishments.messaging.redis;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.ruinscraft.punishments.messaging.Message;
import com.ruinscraft.punishments.messaging.MessageConsumer;
import redis.clients.jedis.JedisPubSub;

public class RedisMessageConsumer extends JedisPubSub implements MessageConsumer {

    private static final Gson GSON = new Gson();

    @Override
    public void onMessage(String channel, String messageRaw) {
        if (!channel.equals(RedisMessageManager.REDIS_CHANNEL)) {
            return;
        }

        Message message = null;

        try {
            message = GSON.fromJson(messageRaw, Message.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        if (message != null) consume(message);
    }

}
