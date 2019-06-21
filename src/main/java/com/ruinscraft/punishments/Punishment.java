package com.ruinscraft.punishments;

import java.util.UUID;

public class Punishment {

    private int punishmentId;
    private UUID punisher;
    private String offender; // UUID, IP, etc
    private String reason;
    private long duration;

}
