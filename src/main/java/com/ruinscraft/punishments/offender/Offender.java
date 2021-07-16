package com.ruinscraft.punishments.offender;

public abstract class Offender<IDENTIFIER> {

    protected IDENTIFIER identifier;

    public Offender(IDENTIFIER identifier) {
        this.identifier = identifier;
    }

    public IDENTIFIER getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    public abstract boolean isOnline();

    public abstract void sendMessage(String msg);

    public abstract void kick(String kickMsg);

}
