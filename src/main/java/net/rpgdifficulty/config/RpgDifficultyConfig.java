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

    @Comment("in Blocks")
    public int increasingDistance = 300;
    @Comment("0.1 = 10%")
    public double distanceFactor = 0.1D;

    @Comment("in minutes")
    public int increasingTime = 60;
    @Comment("0.05 = 5%")
    public double timeFactor = 0.05D;

    @Comment("in Blocks")
    public int heightDistance = 30;
    @Comment("0.1 = 10%")
    public double heightFactor = 0.1D;

    @Comment("2.0 = double")
    public double maxFactorHealth = 3.0D;
    public double maxFactorDamage = 3.0D;
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

    public double startingFactor = 1.0D;
    @Comment("in Blocks")
    public int startingDistance = 0;
    @Comment("in minutes")
    public int startingTime = 0;
    @Comment("in blocks, sea level is 62")
    public int startingHeight = 62;

    public boolean positiveHeightIncreasion = true;
    public boolean negativeHeightIncreasion = true;

    public boolean affectBosses = true;
    @Comment("Applies only for dimensions other than Overworld")
    public boolean excludeDistanceInOtherDimension = true;
    @Comment("Applies only for dimensions other than Overworld")
    public boolean excludeTimeInOtherDimension = true;
    @Comment("Applies only for dimensions other than Overworld")
    public boolean excludeHeightInOtherDimension = true;
    @Comment("Based on level - Requires Nameplate mod addon")
    public boolean dropMoreLoot = false;
    @Comment("0.02 = +2% chance per lvl")
    public float moreLootChance = 0.02F;
    public float maxLootChance = 0.7F;
    @Comment("Each loot table item has 0.5 = 50% chance to get dropped")
    public float chanceForEachItem = 0.5F;
    @Comment("Only applies when LevelZ is loaded. 0.0 = disabled")
    public double levelZLevelFactor = 0.0D;
    @Comment("Only applies when LevelZ is loaded. Get players in radius")
    public double levelZPlayerRadius = 100.0D;

    @Comment("Hud for testing purpose only")
    public boolean hudTesting = false;

    @Comment("Excluded Entity List Bsp: minecraft:villager")
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
    public float bigZombieSize = 1.3F;
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
    @ConfigEntry.Category("monster_setting")
    @Comment("Additional factor to prevent extrem explosions")
    public double creeperExplosionFactor = 1.0D;
}