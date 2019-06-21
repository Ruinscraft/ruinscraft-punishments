package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;

import java.util.List;
import java.util.concurrent.Callable;

public interface Storage {

    Callable<Void> insert(PunishmentEntry entry);

    Callable<Void> delete(int punishmentId);

    Callable<List<PunishmentEntry>> query(String offender);

    Callable<List<Punishment>> queryByType(String offender, PunishmentType type);

}
