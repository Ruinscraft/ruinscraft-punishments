package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.Punishment;

import java.util.List;
import java.util.concurrent.Callable;

public interface PunishmentStorage {

    Callable<Void> insert(Punishment punishment);

    Callable<List<Punishment>> query(String offender);

    Callable<Void> delete(int punishmentId);

}
