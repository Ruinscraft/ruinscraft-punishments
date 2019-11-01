package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.storage.Storage;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class PunishmentProfiles {

    private static Storage storage = PunishmentsPlugin.get().getStorage();
    private static Set<PunishmentProfile> profiles = new HashSet<>();

    public static <IDENTITY> Optional<PunishmentProfile> getProfile(IDENTITY identity) {
        // TODO: clean up this filter
        return profiles.stream().filter(profile -> profile.offender.equals(identity) || profile.offender.getIdentifier().equals(identity)).findFirst();
    }

    public static <IDENTITY> CompletableFuture<PunishmentProfile> getOrLoadProfile(IDENTITY identity, Class<? extends Offender> offenderClass) {
        {
            Optional<PunishmentProfile> profile = getProfile(identity);

            if (profile.isPresent()) {
                return CompletableFuture.completedFuture(profile.get());
            }
        }

        Offender offender = null;

        try {
            offender = offenderClass.getDeclaredConstructor(identity.getClass()).newInstance(identity);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (offender == null) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<PunishmentProfile> future = new CompletableFuture<>();
        PunishmentProfile profile = new PunishmentProfile(offender);

        profiles.add(profile);

        storage.queryOffender(offender).thenAcceptAsync(entries -> {
            entries.forEach(entry -> profile.punishments.put(entry.punishment.getPunishmentId(), entry));
            future.complete(profile);
        });

        return future;
    }

    public static <IDENTITY> void unload(IDENTITY identity) {
        getProfile(identity).ifPresent(profile -> profiles.remove(profile));
    }

    public static void clear() {
        profiles.clear();
    }

}
