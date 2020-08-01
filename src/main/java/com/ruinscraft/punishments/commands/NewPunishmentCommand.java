package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Duration;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NewPunishmentCommand extends PunishmentCommandExecutor implements TabCompleter {

    private static final String[] VALID_TIME_LENGTHS = new String[]{
            "10m",
            "30m",
            "1h",
            "3h",
            "6h",
            "12h",
            "1d",
            "3d",
            "7d",
            "14d",
            "30d",
            "60d"
    };

    public NewPunishmentCommand() {
        super(true, false);
    }

    @Override
    protected boolean run(CommandSender sender, String label, String[] args) {
        final PunishmentType type = PunishmentType.match(label);

        if (type == null) {
            throw new IllegalStateException("PunishmentType was null");
        }

        int minArgs = 2 + (isTemporary() ? 1 : 0);

        if (args.length < minArgs) {
            String help = "/" + label + " <username>" + (isTemporary() ? " <duration>" : "") + " <reason>";
            sender.sendMessage(help);
            return true;
        }

        final UUID punisher;

        if (sender instanceof Player) {
            punisher = ((Player) sender).getUniqueId();
        } else {
            punisher = console.UUID;
        }

        final long duration;
        final String reason;

        if (isTemporary()) {
            duration = Duration.getDurationFromWords(args[1]);
            reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        } else {
            duration = -1;
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        CompletableFuture.runAsync(() -> {
            PunishmentProfile profile;

            if (isIp()) {
                profile = PunishmentProfiles.getOrLoadProfile(args[0], IPOffender.class).join();
            } else {
                UUID targetUUID = PlayerLookups.getUniqueId(args[0]).join();
                profile = PunishmentProfiles.getOrLoadProfile(targetUUID, UUIDOffender.class).join();
            }

            if (profile == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Could not load Punishment Profile for " + args[0]);
                return;
            }

            if (profile.isAlready(type)) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " is already " + type.getVerb() + ".");
                return;
            }

            if (profile.wasRecentlyPunished()) {
                sender.sendMessage(Messages.COLOR_WARN + "This address was just punished. Wait a few seconds.");
                return;
            }

            String context = PunishmentsPlugin.get().getServerContext();

            Punishment.builder()
                    .serverContext(context)
                    .punisher(punisher)
                    .offender(profile.getOffender())
                    .offenderUsername(args[0])
                    .punisherUsername(sender.getName())
                    .duration(duration)
                    .reason(reason)
                    .build()
                    .entry(type)
                    .performAction(PunishmentAction.CREATE);
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> suggestions;

        // 0        1           2   3
        // tempmute royalkingkb 10m test

        boolean temporary = label.startsWith("temp");

        if (args.length == 1) {
            suggestions = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2 && temporary) {
            suggestions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], Arrays.asList(VALID_TIME_LENGTHS), suggestions);
        } else {
            suggestions = new ArrayList<>();
        }

        return suggestions;
    }

}
