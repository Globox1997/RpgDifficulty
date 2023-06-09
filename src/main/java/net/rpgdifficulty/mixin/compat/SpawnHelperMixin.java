package net.rpgdifficulty.mixin.compat;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@SuppressWarnings({ "unused" })
@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {

    // @Inject(method =
    // "Lnet/minecraft/world/SpawnHelper;spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
    // at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    // private static void spawnEntitiesInChunkMixin(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, SpawnHelper.Checker checker, SpawnHelper.Runner runner, CallbackInfo info,
    // StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, int i, BlockState blockState, BlockPos.Mutable mutable, int j, int k, int l, int m, int n,
    // SpawnSettings.SpawnEntry spawnEntry, EntityData entityData, int o, int p, int q, double d, double e, PlayerEntity playerEntity, double f, MobEntity mobEntity) {
    // System.out.println("SpawnHelper: " + mobEntity + " : " + playerEntity);

    // }

    // @Inject(method = "populateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;refreshPositionAndAngles(DDDFF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    // private static void populateEntities(ServerWorldAccess world, RegistryEntry<Biome> biomeEntry, ChunkPos chunkPos, Random random, CallbackInfo info, SpawnSettings spawnSettings, Pool pool, int
    // i,
    // int j, Optional optional, SpawnSettings.SpawnEntry spawnEntry, int k, EntityData entityData, int l, int m, int n, int o, int p, boolean bl, int q, BlockPos blockPos, float f, double d,
    // double e, Entity entity) {
    // }
}
