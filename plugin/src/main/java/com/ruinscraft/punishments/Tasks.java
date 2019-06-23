package com.ruinscraft.punishments;

public final class Tasks {

    private static final PunishmentsPlugin plugin = PunishmentsPlugin.get();

    public static void async(Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static void sync(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

}
