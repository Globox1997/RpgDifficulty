package net.rpgdifficulty.config;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "rpgdifficulty")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class RpgDifficultyConfig implements ConfigData {

    @Comment("Distance in Blocks")
    public int increasingDistance = 300;
    @Comment("0.1 = 10%")
    public double distanceFactor = 0.1D;

    @Comment("in minutes")
    public int increasingTime = 30;
    @Comment("0.1 = 10%")
    public double timeFactor = 0.1D;

    @Comment("2.0 = 200% = double")
    public double maxFactorHealth = 2.0D;
    public double maxFactorDamage = 2.0D;
    public double maxFactorProtection = 1.5D;
    @Comment("Applies only to special zombie")
    public double maxFactorSpeed = 2.0D;

    public boolean allowRandomValues = false;
    @Comment("in %")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int randomChance = 10;
    @Comment("in %")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int randomFactor = 10;

    @Comment("Based on other settings")
    public boolean extraXp = true;
    public double maxXPFactor = 2.0D;

    public boolean affectBosses = true;
    @Comment("Applies only for dimensions other than Overworld")
    public boolean excludeDistanceInOtherDimension = true;

    @Comment("Hud for testing purpose only")
    public boolean hudTesting = false;

    @Comment("Exluded Entity List Bsp: minecraft.villager")
    public List<String> excluded_entity = new ArrayList<>();

    @ConfigEntry.Category("monster_setting")
    public boolean allowSpecialZombie = true;
    @ConfigEntry.Category("monster_setting")
    @Comment("in %")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int bigZombieChance = 10;
    @ConfigEntry.Category("monster_setting")
    public float bigZombieSlownessFactor = 0.7F;
    @ConfigEntry.Category("monster_setting")
    public int bigZombieBonusLifePoints = 10;
    @ConfigEntry.Category("monster_setting")
    public int bigZombieBonusDamage = 2;
    @ConfigEntry.Category("monster_setting")
    @Comment("in %")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int speedZombieChance = 10;
    @ConfigEntry.Category("monster_setting")
    public double speedZombieSpeedFactor = 1.3D;
    @ConfigEntry.Category("monster_setting")
    public int speedZombieMalusLifePoints = 10;
    @ConfigEntry.Category("monster_setting")
    @Comment("Each player increases boss attributes")
    public boolean dynamicBossModification = true;
    @ConfigEntry.Category("monster_setting")
    @Comment("0.2 = 20% per player")
    public double dynamicBossModificator = 0.3D;
    @ConfigEntry.Category("monster_setting")
    public double bossMaxFactor = 3.0D;
    @ConfigEntry.Category("monster_setting")
    public double bossDistanceFactor = 0.0D;
    @ConfigEntry.Category("monster_setting")
    public double bossTimeFactor = 0.1D;
}