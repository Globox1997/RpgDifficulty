package net.rpgdifficulty.zone;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DifficultyZoneCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("rpgdifficulty")
                .then(literal("zone")
                        .then(literal("create")
                                .then(literal("box")
                                        .then(argument("pos1", BlockPosArgument.blockPos())
                                                .then(argument("pos2", BlockPosArgument.blockPos())
                                                        .then(argument("factor", DoubleArgumentType.doubleArg(0.0))
                                                                .requires(source -> source.hasPermission(2))
                                                                .executes(ctx -> createBox(ctx, null))
                                                                .then(argument("name", StringArgumentType.string())
                                                                        .executes(ctx -> createBox(ctx,
                                                                                StringArgumentType.getString(ctx, "name"))))))))
                                .then(literal("sphere")
                                        .then(argument("center", BlockPosArgument.blockPos())
                                                .then(argument("radius", DoubleArgumentType.doubleArg(0.0))
                                                        .then(argument("factor", DoubleArgumentType.doubleArg(0.0))
                                                                .requires(source -> source.hasPermission(2))
                                                                .executes(ctx -> createSphere(ctx, null))
                                                                .then(argument("name", StringArgumentType.string())
                                                                        .executes(ctx -> createSphere(ctx,
                                                                                StringArgumentType.getString(ctx, "name")))))))))
                        .then(literal("remove")
                                .requires(source -> source.hasPermission(2))
                                .then(argument("id", UuidArgument.uuid())
                                        .executes(DifficultyZoneCommand::remove))
                                .then(literal("here")
                                        .executes(DifficultyZoneCommand::removeHere)))
                        .then(literal("list")
                                .requires(source -> source.hasPermission(2))
                                .executes(DifficultyZoneCommand::list))));
    }

    private static int createBox(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        BlockPos pos1 = BlockPosArgument.getBlockPos(context, "pos1");
        BlockPos pos2 = BlockPosArgument.getBlockPos(context, "pos2");
        double factor = DoubleArgumentType.getDouble(context, "factor");

        String dimension = source.getLevel().dimension().location().toString();
        DifficultyZone zone = DifficultyZone.createBox(dimension, pos1, pos2, factor, name);

        DifficultyZonePersistentState.get(source.getServer()).addZone(zone);
        ZoneSyncManager.syncToAll(source.getServer());

        source.sendSuccess(() -> Component.translatable("commands.rpgdifficulty.difficulty_zone_created", zone.describe(), zone.getId().toString()), true);
        return 1;
    }

    private static int createSphere(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        BlockPos center = BlockPosArgument.getBlockPos(context, "center");
        double radius = DoubleArgumentType.getDouble(context, "radius");
        double factor = DoubleArgumentType.getDouble(context, "factor");

        String dimension = source.getLevel().dimension().location().toString();
        DifficultyZone zone = DifficultyZone.createSphere(dimension, center, radius, factor, name);

        DifficultyZonePersistentState.get(source.getServer()).addZone(zone);
        ZoneSyncManager.syncToAll(source.getServer());

        source.sendSuccess(() -> Component.translatable("commands.rpgdifficulty.difficulty_zone_created", zone.describe(), zone.getId().toString()), true);
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        UUID id = UuidArgument.getUuid(context, "id");

        boolean removed = DifficultyZonePersistentState.get(source.getServer()).removeZone(id);

        if (removed) {
            ZoneSyncManager.syncToAll(source.getServer());
            source.sendSuccess(() -> Component.translatable("commands.rpgdifficulty.difficulty_zone_deleted", id), true);
            return 1;
        } else {
            source.sendFailure(Component.translatable("commands.rpgdifficulty.difficulty_zone_not_found", id));
            return 0;
        }
    }

    private static int removeHere(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        BlockPos pos = BlockPos.containing(source.getPosition());
        String dimension = source.getLevel().dimension().location().toString();

        DifficultyZonePersistentState state = DifficultyZonePersistentState.get(source.getServer());
        Optional<DifficultyZone> found = state.findZone(dimension, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        if (found.isEmpty()) {
            source.sendFailure(Component.translatable("commands.rpgdifficulty.difficulty_zone_unavailable"));
            return 0;
        }

        DifficultyZone zone = found.get();
        state.removeZone(zone.getId());
        ZoneSyncManager.syncToAll(source.getServer());

        source.sendSuccess(() -> Component.translatable("commands.rpgdifficulty.difficulty_zone_deleted_2", zone.describe(), zone.getId().toString()), true);
        return 1;
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        List<DifficultyZone> zones = DifficultyZonePersistentState.get(source.getServer()).getZones();

        if (zones.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.rpgdifficulty.difficulty_zone_undefined"), false);
            return 0;
        }

        source.sendSuccess(() -> Component.translatable("commands.rpgdifficulty.difficulty_zone_defined", zones.size()), false);
        for (DifficultyZone zone : zones) {
            source.sendSuccess(() -> Component.translatable("commands.rpgdifficulty.difficulty_zone_entry", zone.getId().toString(), zone.describe()), false);
        }
        return zones.size();
    }
}