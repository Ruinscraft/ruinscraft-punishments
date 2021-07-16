package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.storage.PunishmentStorage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class PunishmentProfiles {

    private static Map<Offender<?>, PunishmentProfile> profiles = new ConcurrentHashMap<>();

    public static CompletableFuture<PunishmentProfile> getOrLoadProfile(Offender<?> offender) {
        if (profiles.containsKey(offender)) {
            return CompletableFuture.completedFuture(profiles.get(offender));
        }

        PunishmentStorage storage = PunishmentsPlugin.get().getStorage();

        return storage.queryOffender(offender).thenApplyAsync(entries -> {
            PunishmentProfile profile = new PunishmentProfile(offender);

            entries.forEach(entry -> profile.punishments.put(entry.punishment.getPunishmentId(), entry));

            if (offender instanceof UUIDOffender) {
                UUIDOffender uuidOffender = (UUIDOffender) offender;
                uuidOffender.loadAddressLogs().join();
            }

            // Accept to cache
            profiles.put(offender, profile);

            return profile;
        });
    }

}
