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

    // stolen from PlotSquared (mostly)
    public static long getMillisFromWords(String string) {
        string = string.toLowerCase().trim().toLowerCase();
        String[] split = string.split(" ");
        long time = 0;
        for (String value : split) {
            int nums = Integer.parseInt(value.replaceAll("[^\\d]", ""));
            String letters = value.replaceAll("[^a-z]", "");
            switch (letters) {
                case "week":
                case "weeks":
                case "wks":
                case "w":
                    time += 604800 * nums;
                    break;
                case "days":
                case "day":
                case "d":
                    time += 86400 * nums;
                    break;
                case "hour":
                case "hr":
                case "hrs":
                case "hours":
                case "h":
                    time += 3600 * nums;
                    break;
                case "minutes":
                case "minute":
                case "mins":
                case "min":
                case "m":
                    time += 60 * nums;
                    break;
                case "seconds":
                case "second":
                case "secs":
                case "sec":
                case "s":
                    time += nums;
                    break;
            }
        }
        return time * 1000;
    }

}
