package com.ruinscraft.punishments.messaging;

import java.util.concurrent.CompletableFuture;

public interface MessageDispatcher {

    CompletableFuture<Void> dispatch(Message message);

}
