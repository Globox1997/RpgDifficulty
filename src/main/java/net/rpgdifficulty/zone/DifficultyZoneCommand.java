package net.rpgdifficulty.zone;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;
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
                                                                .executes(ctx -> createBox(ctx, null))
                                                                .then(argument("name", StringArgumentType.string())
                                                                        .executes(ctx -> createBox(ctx,
                                                                                StringArgumentType.getString(ctx, "name"))))))))
                                .then(literal("sphere")
                                        .then(argument("center", BlockPosArgumentType.blockPos())
                                                .then(argument("radius", DoubleArgumentType.doubleArg(0.0))
                                                        .then(argument("factor", DoubleArgumentType.doubleArg(0.0))
                                                                .requires(source -> source.hasPermissionLevel(2))
                                                                .executes(ctx -> createSphere(ctx, null))
                                                                .then(argument("name", StringArgumentType.string())
                                                                        .executes(ctx -> createSphere(ctx,
                                                                                StringArgumentType.getString(ctx, "name")))))))))
                        .then(literal("remove")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(argument("id", UuidArgumentType.uuid())
                                        .executes(DifficultyZoneCommand::remove))
                                .then(literal("here")
                                        .executes(DifficultyZoneCommand::removeHere)))
                        .then(literal("list")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(DifficultyZoneCommand::list))));
    }

    private static int createBox(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
        BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");
        double factor = DoubleArgumentType.getDouble(context, "factor");

        String dimension = source.getWorld().getRegistryKey().getValue().toString();
        DifficultyZone zone = DifficultyZone.createBox(dimension, pos1, pos2, factor, name);

        DifficultyZonePersistentState.get(source.getServer()).addZone(zone);
        ZoneSyncManager.syncToAll(source.getServer());

        source.sendFeedback(() -> Text.translatable("commands.rpgdifficulty.difficulty_zone_created", zone.describe(), zone.getId().toString()), true);
        return 1;
    }

    private static int createSphere(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        BlockPos center = BlockPosArgumentType.getBlockPos(context, "center");
        double radius = DoubleArgumentType.getDouble(context, "radius");
        double factor = DoubleArgumentType.getDouble(context, "factor");

        String dimension = source.getWorld().getRegistryKey().getValue().toString();
        DifficultyZone zone = DifficultyZone.createSphere(dimension, center, radius, factor, name);

        DifficultyZonePersistentState.get(source.getServer()).addZone(zone);
        ZoneSyncManager.syncToAll(source.getServer());

        source.sendFeedback(() -> Text.translatable("commands.rpgdifficulty.difficulty_zone_created", zone.describe(), zone.getId().toString()), true);
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        UUID id = UuidArgumentType.getUuid(context, "id");

        boolean removed = DifficultyZonePersistentState.get(source.getServer()).removeZone(id);

        if (removed) {
            ZoneSyncManager.syncToAll(source.getServer());
            source.sendFeedback(() -> Text.translatable("commands.rpgdifficulty.difficulty_zone_deleted", id), true);
            return 1;
        } else {
            source.sendError(Text.translatable("commands.rpgdifficulty.difficulty_zone_not_found", id));
            return 0;
        }
    }

    private static int removeHere(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        BlockPos pos = BlockPos.ofFloored(source.getPosition());
        String dimension = source.getWorld().getRegistryKey().getValue().toString();

        DifficultyZonePersistentState state = DifficultyZonePersistentState.get(source.getServer());
        Optional<DifficultyZone> found = state.findZone(dimension, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        if (found.isEmpty()) {
            source.sendError(Text.translatable("commands.rpgdifficulty.difficulty_zone_unavailable"));
            return 0;
        }

        DifficultyZone zone = found.get();
        state.removeZone(zone.getId());
        ZoneSyncManager.syncToAll(source.getServer());

        source.sendFeedback(() -> Text.translatable("commands.rpgdifficulty.difficulty_zone_deleted_2", zone.describe(), zone.getId().toString()), true);
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        List<DifficultyZone> zones = DifficultyZonePersistentState.get(source.getServer()).getZones();

        if (zones.isEmpty()) {
            source.sendFeedback(() -> Text.translatable("commands.rpgdifficulty.difficulty_zone_undefined"), false);
            return 0;
        }

        source.sendFeedback(() -> Text.translatable("commands.rpgdifficulty.difficulty_zone_defined", zones.size()), false);
        for (DifficultyZone zone : zones) {
            source.sendFeedback(() -> Text.literal("- " + zone.getId().toString() + ": " + zone.describe()), false);
        }
        return zones.size();
    }
}