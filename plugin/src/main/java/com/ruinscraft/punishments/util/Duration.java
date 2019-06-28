package com.ruinscraft.punishments.util;

import com.ruinscraft.punishments.Punishment;
import org.apache.commons.lang.time.DurationFormatUtils;

public final class Duration {

    public static String getRemainingDurationWords(Punishment punishment) {
        if (punishment.getTimeLeftMillis() == 0L) {
            return "never";
        }
        return DurationFormatUtils.formatDurationWords(punishment.getTimeLeftMillis(), true, true);
    }

    public static String getTotalDurationWords(Punishment punishment) {
        if (punishment.getExpirationTime() == -1) {
            return "forever";
        }
        final long duration = punishment.getExpirationTime() - punishment.getInceptionTime();
        return DurationFormatUtils.formatDurationWords(duration, true, true);
    }

}
