package net.rpgdifficulty.mixin.compat;

import com.yungnickyoung.minecraft.travelerstitles.render.TitleRenderManager;
import com.yungnickyoung.minecraft.travelerstitles.render.TitleRenderer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.nameplate.util.NameplateTracker;
import net.rpgdifficulty.api.MobStrengthener;

@Environment(EnvType.CLIENT)
@Mixin(TitleRenderManager.class)
public class TitleRenderManagerMixin {

    @Shadow
    @Mutable
    @Final
    public TitleRenderer<Biome> biomeTitleRenderer;

    @Inject(method = "updateBiomeTitle", at = @At(value = "INVOKE", target = "Lcom/yungnickyoung/minecraft/travelerstitles/render/TitleRenderer;addRecentEntry(Ljava/lang/Object;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void updateBiomeTitleMixin(World world, BlockPos playerPos, PlayerEntity player, boolean isPlayerUnderground, CallbackInfo info, RegistryEntry<?> biomeHolder, boolean isUndergroundBiome,
            Identifier biomeBaseKey, String overrideBiomeNameKey, String normalBiomeNameKey, Text biomeTitle) {

        SkeletonEntity skeletonEntity = EntityType.SKELETON.create(world);
        skeletonEntity.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0.0f, 0.0f);
        MobStrengthener.changeAttributes(skeletonEntity, world);

        this.biomeTitleRenderer.displayTitle(biomeTitle, Text.translatable("text.rpgdifficulty.title", NameplateTracker.getMobLevel(skeletonEntity)));
        skeletonEntity.discard();
    }
}
