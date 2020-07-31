package com.ruinscraft.punishments.offender;

import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.Bukkit;

import java.util.UUID;

public class OnlineUUIDOffender extends UUIDOffender implements OnlineOffender {

    public OnlineUUIDOffender(UUID uuid) {
        super(uuid);
    }

    @Override
    public void kick(String kickMsg) {
        Tasks.sync(() -> Bukkit.getPlayer(identifier).kickPlayer(kickMsg));
    }

    @Override
    public void sendMessage(String msg) {
        Bukkit.getPlayer(identifier).sendMessage(msg);
    }

}
