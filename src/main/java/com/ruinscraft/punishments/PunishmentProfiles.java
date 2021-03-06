package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.offender.UUIDOffender;
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

        PunishmentProfile profile = new PunishmentProfile(offender);

        storage.queryOffender(offender).join().forEach(entry -> profile.punishments.put(entry.punishment.getPunishmentId(), entry));

        // If the offender is a UUIDOffender, load it's addresses
        if (offender instanceof UUIDOffender) {
            UUIDOffender uuidOffender = (UUIDOffender) offender;
            uuidOffender.loadAddressLogs().join();
        }

        profiles.add(profile);

        return CompletableFuture.completedFuture(profile);
    }

    public static <IDENTITY> void unload(IDENTITY identity) {
        getProfile(identity).ifPresent(profile -> profiles.remove(profile));
    }

    public static void clear() {
        profiles.clear();
    }

}
