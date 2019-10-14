package com.ruinscraft.punishments.offender;

public class IPOffender extends Offender<String> { // using String to support ipv6

    public IPOffender(String ip) {
        super(ip);
    }

}
