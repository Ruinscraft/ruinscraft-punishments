package com.ruinscraft.punishments.offender;

import java.util.UUID;

public class OnlineUUIDOffender extends UUIDOffender implements OnlineOffender {

    public OnlineUUIDOffender(UUID uuid) {
        super(uuid);
    }

    @Override
    public void kick(String kickMsg) {
        // TODO:
    }

    @Override
    public void sendMessage(String msg) {
        // TODO:
    }

}
