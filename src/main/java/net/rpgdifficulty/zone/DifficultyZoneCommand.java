package net.rpgdifficulty.zone;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DifficultyZoneCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("rpgdifficulty")
                .then(literal("zone")
                        .then(literal("create")
                                .then(literal("box")
                                        .then(argument("pos1", BlockPosArgumentType.blockPos())
                                                .then(argument("pos2", BlockPosArgumentType.blockPos())
                                                        .then(argument("factor", DoubleArgumentType.doubleArg(0.0))
                                                                .requires(source -> source.hasPermissionLevel(2))
                                                                .executes(DifficultyZoneCommand::createBox)))))
                                .then(literal("sphere")
                                        .then(argument("center", BlockPosArgumentType.blockPos())
                                                .then(argument("radius", DoubleArgumentType.doubleArg(0.0))
                                                        .then(argument("factor", DoubleArgumentType.doubleArg(0.0))
                                                                .requires(source -> source.hasPermissionLevel(2))
                                                                .executes(DifficultyZoneCommand::createSphere))))))
                        .then(literal("remove")
                                .then(argument("id", UuidArgumentType.uuid())
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(DifficultyZoneCommand::remove)))
                        .then(literal("list")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(DifficultyZoneCommand::list))));
    }

    private static int createBox(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
        BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");
        double factor = DoubleArgumentType.getDouble(context, "factor");

        String dimension = source.getWorld().getRegistryKey().getValue().toString();
        DifficultyZone zone = DifficultyZone.createBox(dimension, pos1, pos2, factor);

        DifficultyZonePersistentState.get(source.getServer()).addZone(zone);

        source.sendFeedback(() -> Text.literal("Difficulty-Zone erstellt: " + zone.describe() + " (ID: " + zone.getId() + ")"), true);
        return 1;
    }

    private static int createSphere(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        BlockPos center = BlockPosArgumentType.getBlockPos(context, "center");
        double radius = DoubleArgumentType.getDouble(context, "radius");
        double factor = DoubleArgumentType.getDouble(context, "factor");

        String dimension = source.getWorld().getRegistryKey().getValue().toString();
        DifficultyZone zone = DifficultyZone.createSphere(dimension, center, radius, factor);

        DifficultyZonePersistentState.get(source.getServer()).addZone(zone);

        source.sendFeedback(() -> Text.literal("Difficulty-Zone erstellt: " + zone.describe() + " (ID: " + zone.getId() + ")"), true);
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        UUID id = UuidArgumentType.getUuid(context, "id");

        boolean removed = DifficultyZonePersistentState.get(source.getServer()).removeZone(id);

        if (removed) {
            source.sendFeedback(() -> Text.literal("Difficulty-Zone " + id + " wurde entfernt."), true);
            return 1;
        } else {
            source.sendError(Text.literal("Keine Difficulty-Zone mit der ID " + id + " gefunden."));
            return 0;
        }
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        List<DifficultyZone> zones = DifficultyZonePersistentState.get(source.getServer()).getZones();

        if (zones.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Es sind keine Difficulty-Zonen definiert."), false);
            return 0;
        }

        source.sendFeedback(() -> Text.literal("Aktive Difficulty-Zonen (" + zones.size() + "):"), false);
        for (DifficultyZone zone : zones) {
            source.sendFeedback(() -> Text.literal("- " + zone.getId() + ": " + zone.describe()), false);
        }
        return zones.size();
    }
}