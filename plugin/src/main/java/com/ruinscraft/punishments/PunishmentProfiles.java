package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.storage.Storage;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class PunishmentProfiles {

    private static Storage storage = PunishmentsPlugin.get().getStorage();
    private static Set<PunishmentProfile> profiles = new HashSet<>();

    /***
     * Returns a PunishmentProfile or null if not loaded
     * @param identity
     * @return PunishmentProfile, may be null
     */
    public static <IDENTITY> PunishmentProfile getProfile(IDENTITY identity) {
        return profiles.stream().filter(profile -> profile.offender.getIdentifier().equals(identity)).findFirst().get();
    }

    /***
     * Gets (if already loaded) or loads a PunishmentProfile
     * @param identity
     * @return CompletableFuture containing the PunishmentProfile
     */
    public static <IDENTITY> CompletableFuture<PunishmentProfile> getOrLoadProfile(IDENTITY identity, Class<? extends Offender> offenderClass) {
        return CompletableFuture.supplyAsync(() -> {
            {
                final PunishmentProfile profile = getProfile(identity);

                if (profile != null) {
                    return profile;
                }
            }

            Offender offender = null;

            try {
                offender = offenderClass.getDeclaredConstructor().newInstance(identity);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            final PunishmentProfile profile = new PunishmentProfile(offender);

            storage.queryOffender(offender)
                    .thenAcceptAsync(entries ->
                            entries.forEach(entry ->
                                    profile.punishments.put(entry.punishment.getPunishmentId(), entry)));

            return profile;
        });
    }

    /***
     * Unloads a potentially loaded PunishmentProfile for an identity
     * @param identity
     */
    public static <IDENTITY> void unload(IDENTITY identity) {
        PunishmentProfile profile = getProfile(identity);

        if (profile != null) {
            profiles.remove(profile);
        }
    }

}
