package com.ruinscraft.punishments.offender;

public class IPOffender implements Offender<String> {   // using String to support ipv6

    private final String ip;

    public IPOffender(String ip) {
        this.ip = ip;
    }

    @Override
    public String getIdentifier() {
        return ip;
    }

    @Override
    public boolean offerChatMessage(String msg) {
        return false;
    }

    @Override
    public boolean offerKick(String kickMsg) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return ip.equals(obj);
    }

}
