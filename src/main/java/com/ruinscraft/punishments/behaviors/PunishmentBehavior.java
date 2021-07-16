package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public abstract class PunishmentBehavior {

    public String creationMessage(PunishmentEntry entry, boolean staffView) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Messages.COLOR_WARN + entry.punishment.getOffenderUsername());
        joiner.add("has been");
        joiner.add(entry.type.getVerb());
        joiner.add("for");
        joiner.add(entry.punishment.getReason() + ".");
        if (entry.punishment.isTemporary()) {
            joiner.add("Expires in:");
            joiner.add(entry.punishment.getTotalDurationWords());
        }
        if (staffView)
            joiner.add("[STAFFINFO::by=" + entry.punishment.getPunisherUsername() + "]");
        return joiner.toString();
    }

    public void notifyServer(PunishmentEntry entry) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(creationMessage(entry, player.hasPermission("group.helper")));
        }
    }

    public abstract void onCreate(PunishmentEntry entry);

    public abstract void onDelete(PunishmentEntry entry);

    public abstract void onPardon(PunishmentEntry entry);

}
