package com.sxxxp.ALPGMC.common.playerclass;

import com.sxxxp.ALPGMC.core.init.items.ArrowTier;
import net.minecraftforge.common.ForgeConfigSpec;

// 직업 수치 설정입니다. 월드의 serverconfig/alpgmc-server.toml에 저장됩니다.
public class ClassConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    static {
        BUILDER.push("warrior");
    }

    public static final ForgeConfigSpec.DoubleValue WARRIOR_ATTACK_DAMAGE = BUILDER
            .comment("Melee damage bonus (0.4 = +40%)")
            .defineInRange("attackDamageBonus", 0.4D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue WARRIOR_ATTACK_SPEED = BUILDER
            .comment("Attack speed bonus (0.2 = +20%)")
            .defineInRange("attackSpeedBonus", 0.2D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue WARRIOR_KNOCKBACK_RESISTANCE = BUILDER
            .comment("Knockback resistance (0.5 = 50%)")
            .defineInRange("knockbackResistance", 0.5D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.IntValue WARRIOR_ARROW_CRAFT_MULTIPLIER = BUILDER
            .comment("Output multiplier when crafting arrows")
            .defineInRange("arrowCraftMultiplier", 2, 1, 16);
    public static final ForgeConfigSpec.DoubleValue WARRIOR_FOOD_HEALING_PENALTY = BUILDER
            .comment("Natural food healing reduction (0.9 = -90%)")
            .defineInRange("foodHealingPenalty", 0.9D, 0.0D, 1.0D);

    static {
        BUILDER.pop().push("archer");
    }

    public static final ForgeConfigSpec.DoubleValue ARCHER_MOVE_SPEED = BUILDER
            .comment("Move speed bonus (0.15 = +15%)")
            .defineInRange("moveSpeedBonus", 0.15D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue ARCHER_FALL_DAMAGE_REDUCTION = BUILDER
            .comment("Fall damage reduction (0.5 = -50%)")
            .defineInRange("fallDamageReduction", 0.5D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.DoubleValue ARCHER_BOW_DAMAGE = BUILDER
            .comment("Archer bow damage bonus (0.5 = +50%)")
            .defineInRange("bowDamageBonus", 0.5D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue ARCHER_ARROW_SAVE_CHANCE = BUILDER
            .comment("Chance to not consume an arrow (0.3 = 30%). Saved arrows cannot be picked up")
            .defineInRange("arrowSaveChance", 0.3D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.IntValue ARCHER_STARTER_ARROWS = BUILDER
            .comment("Wood arrows given with the starter bow")
            .defineInRange("starterArrows", 32, 0, 64);
    private static final ForgeConfigSpec.DoubleValue WOOD_ARROW_DAMAGE = BUILDER
            .comment("Base damage by arrow tier (vanilla arrow = 2.0)")
            .defineInRange("woodArrowDamage", 1.5D, 0.0D, 100.0D);
    private static final ForgeConfigSpec.DoubleValue FLINT_ARROW_DAMAGE = BUILDER
            .defineInRange("flintArrowDamage", 2.5D, 0.0D, 100.0D);
    private static final ForgeConfigSpec.DoubleValue IRON_ARROW_DAMAGE = BUILDER
            .defineInRange("ironArrowDamage", 3.5D, 0.0D, 100.0D);
    private static final ForgeConfigSpec.DoubleValue DIAMOND_ARROW_DAMAGE = BUILDER
            .defineInRange("diamondArrowDamage", 5.0D, 0.0D, 100.0D);

    static {
        BUILDER.pop().push("healer");
    }

    public static final ForgeConfigSpec.DoubleValue HEALER_ATTACK_DAMAGE_PENALTY = BUILDER
            .comment("Attack damage reduction, except summons (0.9 = -90%)")
            .defineInRange("attackDamagePenalty", 0.9D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.DoubleValue HEALER_FOOD_HEALING_MULTIPLIER = BUILDER
            .comment("Natural food healing multiplier")
            .defineInRange("foodHealingMultiplier", 2.0D, 0.0D, 10.0D);

    static {
        BUILDER.pop().push("tank");
    }

    public static final ForgeConfigSpec.DoubleValue TANK_MAX_HEALTH = BUILDER
            .comment("Extra max health (20 = 10 hearts)")
            .defineInRange("maxHealthBonus", 20.0D, 0.0D, 1000.0D);
    public static final ForgeConfigSpec.DoubleValue TANK_MOVE_SPEED_PENALTY = BUILDER
            .comment("Move speed penalty (0.3 = -30%)")
            .defineInRange("moveSpeedPenalty", 0.3D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.DoubleValue TANK_KNOCKBACK_RESISTANCE = BUILDER
            .comment("Knockback resistance (1.0 = immune)")
            .defineInRange("knockbackResistance", 1.0D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.DoubleValue TANK_DAMAGE_REDUCTION = BUILDER
            .comment("Damage taken reduction (0.3 = -30%)")
            .defineInRange("damageReduction", 0.3D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.DoubleValue TANK_ANVIL_REPAIR_DISCOUNT = BUILDER
            .comment("Repair cost discount at the enhancement anvil (0.5 = -50%). The anvil is not built yet")
            .defineInRange("anvilRepairDiscount", 0.5D, 0.0D, 1.0D);

    static {
        BUILDER.pop().push("level");
    }

    public static final ForgeConfigSpec.IntValue LEVEL_MAX = BUILDER
            .comment("Highest player level")
            .defineInRange("maxLevel", 75, 1, 10000);
    public static final ForgeConfigSpec.IntValue LEVEL_XP_BASE = BUILDER
            .comment("XP needed to go from level 1 to 2")
            .defineInRange("xpBase", 100, 1, 1000000);
    public static final ForgeConfigSpec.IntValue LEVEL_XP_PER_LEVEL = BUILDER
            .comment("Extra XP needed for each level after that")
            .defineInRange("xpPerLevel", 50, 0, 1000000);
    public static final ForgeConfigSpec.IntValue LEVEL_STAT_POINTS = BUILDER
            .comment("Stat points gained per level")
            .defineInRange("statPointsPerLevel", 1, 0, 100);
    public static final ForgeConfigSpec.IntValue LEVEL_MAX_STAT_POINTS = BUILDER
            .comment("Most points one stat can hold")
            .defineInRange("maxPointsPerStat", 50, 1, 10000);
    public static final ForgeConfigSpec.IntValue LEVEL_RESET_COST = BUILDER
            .comment("Vanilla XP levels paid to reset stat points")
            .defineInRange("statResetCost", 30, 0, 10000);
    public static final ForgeConfigSpec.DoubleValue STAT_HEALTH_PER_POINT = BUILDER
            .comment("Max health per point (2 = 1 heart)")
            .defineInRange("healthPerPoint", 1.0D, 0.0D, 100.0D);
    public static final ForgeConfigSpec.DoubleValue STAT_MELEE_PER_POINT = BUILDER
            .comment("Melee damage per point (0.02 = +2%)")
            .defineInRange("meleePerPoint", 0.02D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue STAT_RANGED_PER_POINT = BUILDER
            .comment("Projectile damage per point (0.02 = +2%)")
            .defineInRange("rangedPerPoint", 0.02D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue STAT_DEFENSE_PER_POINT = BUILDER
            .comment("Armor per point")
            .defineInRange("defensePerPoint", 0.5D, 0.0D, 100.0D);
    public static final ForgeConfigSpec.DoubleValue STAT_SPEED_PER_POINT = BUILDER
            .comment("Move speed per point (0.01 = +1%)")
            .defineInRange("speedPerPoint", 0.01D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue STAT_MANA_PER_POINT = BUILDER
            .comment("Max mana per point (mage only)")
            .defineInRange("manaPerPoint", 5.0D, 0.0D, 1000.0D);

    static {
        BUILDER.pop().push("progression");
    }

    public static final ForgeConfigSpec.IntValue TIER_1_LEVEL = BUILDER
            .comment("Level that unlocks choosing a combat branch and the first branch and production abilities")
            .defineInRange("tier1Level", 15, 1, 10000);
    public static final ForgeConfigSpec.IntValue TIER_2_LEVEL = BUILDER
            .defineInRange("tier2Level", 25, 1, 10000);
    public static final ForgeConfigSpec.IntValue TIER_3_LEVEL = BUILDER
            .defineInRange("tier3Level", 35, 1, 10000);
    public static final ForgeConfigSpec.IntValue TIER_4_LEVEL = BUILDER
            .comment("Level that unlocks the ultimate abilities")
            .defineInRange("tier4Level", 45, 1, 10000);
    public static final ForgeConfigSpec.IntValue BRANCH_CHANGE_COST = BUILDER
            .comment("Vanilla XP levels paid to change combat branch. The first choice is free")
            .defineInRange("branchChangeCost", 50, 0, 10000);
    public static final ForgeConfigSpec.DoubleValue BERSERKER_MAX_DAMAGE_BONUS = BUILDER
            .comment("Berserker melee damage bonus as health approaches zero (0.5 = up to +50%)")
            .defineInRange("berserkerMaxDamageBonus", 0.5D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue BERSERKER_KILL_HEAL = BUILDER
            .comment("Health the Berserker regains per kill (2 = 1 heart)")
            .defineInRange("berserkerKillHeal", 2.0D, 0.0D, 100.0D);
    public static final ForgeConfigSpec.DoubleValue SNIPER_FULL_CHARGE_BONUS = BUILDER
            .comment("Sniper damage bonus for fully drawn shots (0.5 = +50%)")
            .defineInRange("sniperFullChargeBonus", 0.5D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue GUARDIAN_DAMAGE_REDUCTION = BUILDER
            .comment("Guardian total damage taken reduction, replacing the tank's base value (0.45 = -45%)")
            .defineInRange("guardianDamageReduction", 0.45D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.IntValue WARRIOR_ARROW_CRAFT_MULTIPLIER_UPGRADED = BUILDER
            .comment("Warrior arrow output multiplier after production tier 2")
            .defineInRange("warriorArrowCraftMultiplierUpgraded", 3, 1, 16);
    public static final ForgeConfigSpec.DoubleValue HUNTER_DROP_CHANCE = BUILDER
            .comment("Archer chance for each mob drop stack to drop twice (0.2 = +20%)")
            .defineInRange("hunterDropChance", 0.2D, 0.0D, 1.0D);
    public static final ForgeConfigSpec.DoubleValue HUNTER_DROP_CHANCE_UPGRADED = BUILDER
            .comment("The same chance after production tier 1")
            .defineInRange("hunterDropChanceUpgraded", 0.5D, 0.0D, 1.0D);

    static {
        BUILDER.pop().push("monster");
    }

    public static final ForgeConfigSpec.DoubleValue MONSTER_HEALTH_PER_LEVEL = BUILDER
            .comment("Mod monster health per level above 1 (0.1 = +10%). Applies when the monster spawns")
            .defineInRange("healthPerLevel", 0.1D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue MONSTER_DAMAGE_PER_LEVEL = BUILDER
            .comment("Mod monster damage per level above 1 (0.05 = +5%). Applies when the monster spawns")
            .defineInRange("damagePerLevel", 0.05D, 0.0D, 10.0D);
    public static final ForgeConfigSpec.DoubleValue MONSTER_XP_PER_LEVEL = BUILDER
            .comment("Mod monster XP reward per level above 1 (0.1 = +10%)")
            .defineInRange("xpPerLevel", 0.1D, 0.0D, 10.0D);

    static {
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double arrowDamage(ArrowTier tier) {
        return switch (tier) {
            case WOOD -> WOOD_ARROW_DAMAGE.get();
            case FLINT -> FLINT_ARROW_DAMAGE.get();
            case IRON -> IRON_ARROW_DAMAGE.get();
            case DIAMOND -> DIAMOND_ARROW_DAMAGE.get();
        };
    }
}
