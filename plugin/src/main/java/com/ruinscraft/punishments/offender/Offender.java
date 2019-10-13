package com.ruinscraft.punishments.offender;

public interface Offender<IDENTIFIER> {

    IDENTIFIER getIdentifier();

    boolean offerChatMessage(String msg);

    boolean offerKick(String kickMsg);

}
