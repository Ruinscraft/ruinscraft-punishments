package com.ruinscraft.punishments.offender;

public class Offender<IDENTIFIER> {

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

}
