package com.ruinscraft.punishments.storage;

import java.sql.Connection;

public interface SQLStorage extends PunishmentStorage {

    Connection getConnection();

}
