package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {

    @Inject(method = "Lnet/minecraft/world/SpawnHelper;spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos,
            SpawnHelper.Checker checker, SpawnHelper.Runner runner, CallbackInfo ci,
            StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, int i, BlockPos.Mutable mutable, int j,
            int k, int l, int m, int n, SpawnSettings.SpawnEntry spawnEntry, EntityData entityData, int o, int p, int q,
            double d, double e, PlayerEntity playerEntity, double f, MobEntity mobEntity) {
        MobStrengthener.changeAttributes(mobEntity, world);
        // Test
        // System.out.println(mobEntity.getType().toString().replace("entity.",
        // "")+"Health:"+mobHealth+"::HealthFactor:"+mobHealthFactor+"::SpawnDistanceDivided:"+spawnDistanceDivided+"::timeDivided:"+timeDivided);
    }

}
