package com.ruinscraft.punishments.offender;

import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class IPOffender extends Offender<String> { // using String to support ipv6

    public IPOffender(String ip) {
        super(ip);
    }

    @Override
    public boolean isOnline() {
        return !getPlayers().isEmpty();
    }

    @Override
    public void kick(String kickMsg) {
        Tasks.sync(() -> getPlayers().forEach(player -> player.kickPlayer(kickMsg)));
    }

    @Override
    public void sendMessage(String msg) {
        Tasks.sync(() -> getPlayers().forEach(player -> player.sendMessage(msg)));
    }

    private Collection<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getAddress().getHostString().equals(identifier)) {
                players.add(onlinePlayer);
            }
        }
        return players;
    }

}
