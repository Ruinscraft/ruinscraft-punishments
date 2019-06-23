package com.ruinscraft.punishments.storage;

import java.sql.Connection;

public interface SQLStorage extends Storage {

    Connection getConnection();

    final class Table {
        protected static final String PUNISHMENTS = "ruinscraft_punishments";
    }

}
