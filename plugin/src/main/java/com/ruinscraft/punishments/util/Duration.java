package com.ruinscraft.punishments.util;

import com.ruinscraft.punishments.Punishment;
import org.apache.commons.lang.time.DurationFormatUtils;

public final class Duration {

    public static String getRemainingDurationWords(Punishment punishment) {
        final long timeLeftMillis = punishment.getTimeLeftMillis();

        if (timeLeftMillis == 0L) {
            return "never";
        }

        // Apache Commons Lang included with Minecraft
        return DurationFormatUtils.formatDurationWords(timeLeftMillis, true, true);
    }

}
