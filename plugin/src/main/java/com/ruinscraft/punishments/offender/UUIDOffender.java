package com.ruinscraft.punishments.offender;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.storage.Storage;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDOffender extends Offender<UUID> {

    private static final Storage storage = PunishmentsPlugin.get().getStorage();

    private transient Set<String> addresses;

    public UUIDOffender(UUID uuid) {
        super(uuid);
        this.addresses = new HashSet<>();
    }

    public CompletableFuture<Void> logAddress(String address) {
        return storage.insertAddress(identifier, address);
    }

    public CompletableFuture<Void> loadAddresses() {
        return storage.queryAddresses(identifier).thenAccept(addresses -> this.addresses = addresses);
    }

    public CompletableFuture<Set<UUIDOffender>> searchAffiliations() {
        return CompletableFuture.supplyAsync(() -> {
            Set<UUIDOffender> affiliations = new HashSet<>();

            for (String address : addresses) {
                storage.queryUsersOnAddress(address).join()
                        .forEach(user -> affiliations.add(new UUIDOffender(user)));
            }

            return affiliations;
        });
    }

    // TODO: clean this up, wow
    public void alertOfEvades() {
        searchAffiliations().thenAccept(affiliations -> {
            for (UUIDOffender affiliation : affiliations) {
                PunishmentProfiles.getOrLoadProfile(affiliation.getIdentifier(), UUIDOffender.class).thenAcceptAsync(affiliatedProfile -> {
                    if (affiliatedProfile.isMuted()) {
                        Punishment mute = affiliatedProfile.getActive(PunishmentType.MUTE);
                        String affiliatedUsername = mute.getOffenderUsername();
                        String username = getUsername().join();

                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.hasPermission("ruinscraft.punishments.viewevaders")) {
                                onlinePlayer.sendMessage(Messages.COLOR_MAIN + username + " is potentially bypassing a mute (" + affiliatedUsername + ")");
                            }
                        }
                    }

                    if (affiliatedProfile.isBanned()) {
                        Punishment ban = affiliatedProfile.getActive(PunishmentType.BAN);
                        String affiliatedUsername = ban.getOffenderUsername();
                        String username = getUsername().join();

                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.hasPermission("ruinscraft.punishments.viewevaders")) {
                                onlinePlayer.sendMessage(Messages.COLOR_MAIN + username + " is potentially bypassing a ban (" + affiliatedUsername + ")");
                            }
                        }
                    }
                });
            }
        });
    }

    public CompletableFuture<String> getUsername() {
        return PlayerLookups.getName(identifier);
    }

    public Set<String> getAddresses() {
        return addresses;
    }

}
