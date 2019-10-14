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
        return identifier.equals(obj);
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

}
