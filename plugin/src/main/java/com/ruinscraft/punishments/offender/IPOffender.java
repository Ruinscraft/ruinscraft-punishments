package com.ruinscraft.punishments.offender;

public class IPOffender extends Offender<String> { // using String to support ipv6

    public IPOffender(String ip) {
        super(ip);
    }

    @Override
    public boolean offerChatMessage(String msg) {
        return false;   // TODO:
    }

    @Override
    public boolean offerKick(String kickMsg) {
        return false;   // TODO:
    }

}
