package dev.cephelo.fisheggs;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final List<String> minecraftColors = Arrays.asList("white", "orange", "magenta", "light_blue",
            "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");

    public static final ModConfigSpec.DoubleValue FOOD_SEARCH_RANGE = BUILDER
            .comment("\n The range a fish will hunt for food")
            .defineInRange("foodSearchRange", 8.0, 0.0, 64.0);

    public static final ModConfigSpec.DoubleValue MATE_SEARCH_RANGE = BUILDER
            .comment("\n The range a fish will hunt for a breeding partner")
            .defineInRange("partnerSearchRange", 8.0, 0.0, 64.0);

    public static final ModConfigSpec.DoubleValue DIST_FOOD = BUILDER
            .comment("\n The maximum squared distance a fish can eat a food item")
            .defineInRange("foodEatRangeSqr", 1.0, 0.0, 64.0);

    public static final ModConfigSpec.DoubleValue DIST_BREED = BUILDER
            .comment("\n The maximum squared distance a fish can breed with their partner")
            .defineInRange("partnerMateRangeSqr", 1.1, 0.0, 64.0);

    public static final ModConfigSpec.IntValue LOVE_TIME = BUILDER
            .comment("\n Ticks that a fish will be in the love state for after eating")
            .defineInRange("inLoveTime", 600, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue REGEN_TIME = BUILDER
            .comment("\n Ticks that a fish will be given regeneration after breeding")
            .defineInRange("regenTime", 100, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue CALM_DOWN_TIME = BUILDER
            .comment("\n Ticks that a fish will ignore food after losing sight of their previous food target")
            .defineInRange("calmdownTime", 600, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BREED_COOLDOWN_TIME = BUILDER
            .comment("\n Ticks that a fish will be unable to breed or hunt food after breeding")
            .defineInRange("breedCooldownTime", 3000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue HATCH_BREED_COOLDOWN_TIME = BUILDER
            .comment("\n Ticks that a fish will be unable to breed or hunt food after hatching")
            .defineInRange("hatchBreedCooldownTime", 12000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue HATCH_TIME = BUILDER
            .comment("\n Ticks that Fish Eggs will take to hatch")
            .defineInRange("hatchTime", 100, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue TROPICAL_COLOR_INNER = BUILDER
            .comment("\n The range a fish will hunt for food")
            .defineInRange("tropicalColorInner", 2, 0, 15);

    public static final ModConfigSpec.IntValue TROPICAL_COLOR_OUTER = BUILDER
            .comment("\n The range a fish will hunt for food")
            .defineInRange("tropicalColorOuter", 1, 0, 15);

    public static final ModConfigSpec.BooleanValue TROPICAL_PATTERN_MUTATION = BUILDER
            .comment("\n Whether hatching tropical fish have a chance of mutating their pattern based on that of one of their parents")
            .define("tropicalPatternMutation", true);

    public static final ModConfigSpec.BooleanValue TROPICAL_SINGLE_PATTERN = BUILDER
            .comment("\n Whether tropical fish that hatch out of the same egg clutch should be identical")
            .define("tropicalSinglePattern", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> FISH_IDS = BUILDER
            .comment("\n List of Entity IDs - each entry corresponds with the entry of fishFood_itemTags with the same index." +
                    "\n   If entry index is greater than fishFood_itemTags length, will retrieve last entry in fishFood_itemTags." +
                    "\n   Must be instance of AbstractFish class.  If an entity ID is not defined here, the default tag fisheggs:fish_food will be used." +
                    "\n   If this list is empty, the tag fisheggs:fish_food will be used by default.")
            .defineListAllowEmpty("fishFood_fishIDs", List.of("minecraft:cod", "minecraft:salmon"), () -> "", Config::validateEntityType);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> FOOD_TAGS = BUILDER
            .comment("\n List of item tags - each entry corresponds with the entry of fishFood_fishIDs with the same index." +
                    "\n   If entry index is greater than fishFood_fishIDs length, will retrieve last entry in fishFood_fishIDs." +
                    "\n   If this list is empty, the tag fisheggs:fish_food will be used by default.")
            .defineListAllowEmpty("fishFood_itemTags", List.of("fisheggs:fish_food"), () -> "", Config::alwaysTrue);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> FISH_BLACKLIST = BUILDER
            .comment("\n List of Entity IDs - fish IDs that should not seek food or breed")
            .defineListAllowEmpty("fishBlacklist", List.of("minecraft:tadpole"), () -> "", Config::validateEntityType);

    public static final ModConfigSpec.BooleanValue FISH_BLACKLIST_IS_WHITELIST = BUILDER
            .comment("\n Whether fishBlacklist should act as a whitelist rather than a blacklist")
            .define("fishBlacklistIsWhitelist", false);

    public static final ModConfigSpec.IntValue MAX_FISH_FROM_EGGS = BUILDER
            .comment("\n The maximum amount of fish that can hatch from a single Fish Eggs item")
            .defineInRange("maxFishFromEggs", 3, 1, 16);

    public static final ModConfigSpec.BooleanValue ENABLE_FISHERMAN_TRADE = BUILDER
            .comment("\n Whether fishBlacklist should act as a whitelist rather than a blacklist")
            .define("enableVillagerTrades", true);

    public static final ModConfigSpec.BooleanValue ENABLE_WANDERING_TRADE = BUILDER
            .comment("\n Whether fishBlacklist should act as a whitelist rather than a blacklist")
            .define("enableWanderingTrade", true);

    public static final ModConfigSpec.IntValue FISH_BREEDING_XP = BUILDER
            .comment("\n Maximum value of the XP orb produced when fish breed; 0 to disable")
            .defineInRange("fishBreedingMaxXP", 5, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue SHOW_TYPE_TOOLTIP = BUILDER
            .comment("\n Whether the Fish Eggs item should display the entity type of the parents")
            .define("showEntityTypeTooltip", true);

    public static final ModConfigSpec.BooleanValue SHOW_VARIANT_TOOLTIP = BUILDER
            .comment("\n Whether the Fish Eggs item should display the tropical pattern data of the parents")
            .define("showVariantTooltip", true);

    public static final ModConfigSpec.BooleanValue HAS_BRED_DESPAWN = BUILDER
            .comment("\n Whether fish can despawn if they've bred.  Existing fish will not despawn if set to true.")
            .define("parentsCanDespawn", false);

    public static final ModConfigSpec.BooleanValue HATCHED_CAN_DESPAWN = BUILDER
            .comment("\n Whether fish can despawn if they hatch from Fish Eggs.  Existing fish will not despawn if set to true.")
            .define("hatchedCanDespawn", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> HUNT_BLACKLIST = BUILDER
            .comment("\n List of Entity IDs - entities that squids will hunt")
            .defineListAllowEmpty("preyBlacklist", List.of("minecraft:cod", "minecraft:salmon", "minecraft:tropical_fish"), () -> "", Config::validateEntityType);

    public static final ModConfigSpec.BooleanValue HUNT_BLACKLIST_IS_WHITELIST = BUILDER
            .comment("\n Whether preyBlacklist should act as a whitelist rather than a blacklist")
            .define("preyBlacklistIsWhitelist", true);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> CANHUNT_BLACKLIST = BUILDER
            .comment("\n List of Entity IDs - Squids that cannot hunt for prey")
            .defineListAllowEmpty("huntBlacklist", List.of(), () -> "", Config::validateEntityType);

    public static final ModConfigSpec.BooleanValue CANHUNT_BLACKLIST_IS_WHITELIST = BUILDER
            .comment("\n Whether huntBlacklist should act as a whitelist rather than a blacklist")
            .define("huntBlacklistIsWhitelist", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> COLOR_LIST = BUILDER
            .comment("\n Color list used in Tropical Fish color mutations.  All strings must match the 16 minecraft colors, or default will be used." +
                    "\n  Default: [\"white\", \"light_gray\", \"gray\", \"black\", \"brown\", " +
                    "\"red\", \"orange\", \"yellow\", \"lime\", \"green\", \"cyan\", \"light_blue\", \"blue\", \"purple\", \"magenta\", \"pink\"]")
            .defineListAllowEmpty("colorMutationList", List.of("white", "light_gray", "gray", "black", "brown",
                    "red", "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink"), () -> "", Config::validateColor);

    public static final ModConfigSpec.DoubleValue HUNT_SEARCH_RANGE = BUILDER
            .comment("\n The range a fish will hunt for food")
            .defineInRange("huntSearchRange", 12.0, 0.0, 64.0);

    public static final ModConfigSpec.DoubleValue SQUID_MATE_SEARCH_RANGE = BUILDER
            .comment("\n The range a squid will hunt for a breeding partner")
            .defineInRange("squidPartnerSearchRange", 12.0, 0.0, 64.0);

    public static final ModConfigSpec.DoubleValue SQUID_DIST_HUNT = BUILDER
            .comment("\n The maximum squared distance a squid can consume prey")
            .defineInRange("squidPreyEatRangeSqr", 1.5, 0.0, 64.0);

    public static final ModConfigSpec.IntValue SQUID_HUNT_DAMAGE = BUILDER
            .comment("\n The amount of damage a squid does when it hits its prey.  Entities with 1hp left will be consumed.")
            .defineInRange("squidHuntDamage", 2, 1, 64);

    public static final ModConfigSpec.DoubleValue SQUID_DIST_BREED = BUILDER
            .comment("\n The maximum squared distance a fish can breed with their partner")
            .defineInRange("squidPartnerMateRangeSqr", 1.5, 0.0, 64.0);

    public static final ModConfigSpec.IntValue SQUID_LOVE_TIME = BUILDER
            .comment("\n Ticks that a fish will be in the love state for after eating")
            .defineInRange("squidInLoveTime", 1200, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SQUID_REGEN_TIME = BUILDER
            .comment("\n Ticks that a fish will be given regeneration after breeding")
            .defineInRange("squidRegenTime", 100, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SQUID_CALM_DOWN_TIME = BUILDER
            .comment("\n Ticks that a fish will ignore food after losing sight of their previous food target")
            .defineInRange("squidCalmdownTime", 1800, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SQUID_BREED_COOLDOWN_TIME = BUILDER
            .comment("\n Ticks that a fish will be unable to breed or hunt food after breeding")
            .defineInRange("squidBreedCooldownTime", 6000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SQUID_HATCH_BREED_COOLDOWN_TIME = BUILDER
            .comment("\n Ticks that a squid will be unable to breed or hunt food after hatching (baby squids don't exist in 1.21.1)")
            .defineInRange("squidHatchBreedCooldownTime", 24000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SQUID_HATCH_TIME = BUILDER
            .comment("\n Ticks that Squid Eggs will take to hatch")
            .defineInRange("squidHatchTime", 100, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SQUID_BREEDING_XP = BUILDER
            .comment("\n Maximum value of the XP orb produced when squid breed; 0 to disable")
            .defineInRange("squidBreedingMaxXP", 8, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue SQUID_HAS_BRED_DESPAWN = BUILDER
            .comment("\n Whether squid can despawn if they've bred.  Existing squid will not despawn if set to true.")
            .define("squidParentsCanDespawn", false);

    public static final ModConfigSpec.BooleanValue SQUID_HATCHED_CAN_DESPAWN = BUILDER
            .comment("\n Whether squid can despawn if they hatch from Squid Eggs.  Existing squid will not despawn if set to true.")
            .define("squidHatchedCanDespawn", false);

    public static final ModConfigSpec.IntValue MAX_SQUID_FROM_EGGS = BUILDER
            .comment("\n The maximum amount of squid that can hatch from a single Squid Eggs item")
            .defineInRange("maxSquidFromEggs", 2, 1, 16);

    public static final ModConfigSpec.BooleanValue DISABLE_FISH_GOALS = BUILDER
            .comment("\n MASTER SWITCH - Disable all behaviors added to fish by this mod.")
            .define("disableFishBehaviors", false);

    public static final ModConfigSpec.BooleanValue DISABLE_SQUID_GOALS = BUILDER
            .comment("\n MASTER SWITCH - Disable all behaviors added to squids by this mod.")
            .define("disableSquidBehaviors", false);

    public static final ModConfigSpec.BooleanValue SQUID_SHOW_TYPE_TOOLTIP = BUILDER
            .comment("\n Whether the Squid Eggs item should display the entity type of the parents")
            .define("squidShowEntityTypeTooltip", true);

    public static final ModConfigSpec.BooleanValue FISH_EGGS_NEED_WATER = BUILDER
            .comment("\n Whether the Squid Eggs item should display the entity type of the parents")
            .define("fishEggsNeedWater", true);

    public static final ModConfigSpec.BooleanValue SQUID_EGGS_NEED_WATER = BUILDER
            .comment("\n Whether the Squid Eggs item should display the entity type of the parents")
            .define("squidEggsNeedWater", true);

    public static final ModConfigSpec.BooleanValue CONSUME_PREY = BUILDER
            .comment("\n Whether Squid should completely consume prey with 1hp remaining; disable to have them kill prey normally")
            .define("squidConsumeWeakPrey", true);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> SQUID_IDS = BUILDER
            .comment("\n List of Entity IDs - each entry corresponds with the entry of squidFood_itemTags with the same index." +
                    "\n   If entry index is greater than squidFood_itemTags length, will retrieve last entry in squidFood_itemTags." +
                    "\n   Must be instance of Squid class.  If an entity ID is not defined here, the default tag fisheggs:squid_food will be used." +
                    "\n   If this list is empty, the tag fisheggs:squid_food will be used by default." +
                    "\n   Note that these lists only make changes if \"squidCanBeHandFed\" is true.")
            .defineListAllowEmpty("squidFood_squidIDs", List.of("minecraft:glow_squid"), () -> "", Config::validateEntityType);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> SQUID_FOOD_TAGS = BUILDER
            .comment("\n List of item tags - each entry corresponds with the entry of squidFood_squidIDs with the same index." +
                    "\n   If entry index is greater than squidFood_squidIDs length, will retrieve last entry in squidFood_squidIDs." +
                    "\n   If this list is empty, the tag fisheggs:squid_food will be used by default." +
                    "\n   Note that these lists only make changes if \"squidCanBeHandFed\" is true.")
            .defineListAllowEmpty("squidFood_itemTags", List.of("fisheggs:squid_food"), () -> "", Config::alwaysTrue);

    public static final ModConfigSpec.BooleanValue SQUID_HANDFED_PREY = BUILDER
            .comment("\n Whether Squid should completely consume prey with 1hp remaining; disable to have them kill prey normally")
            .define("squidCanBeHandFed", true);

    public static final ModConfigSpec.BooleanValue FISH_HANDFED_PREY = BUILDER
            .comment("\n Whether fish should completely consume prey with 1hp remaining; disable to have them kill prey normally")
            .define("fishCanBeHandFed", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> AXOLOTL_BREED_BLACKLIST = BUILDER
            .comment("\n List of Entity IDs - when an axolotl kills one of these, it will go into breeding mode")
            .defineListAllowEmpty("preyBreedBlacklist", List.of("minecraft:tropical_fish"), () -> "", Config::validateEntityType);

    public static final ModConfigSpec.BooleanValue AXOLOTL_BREED_BLACKLIST_IS_WHITELIST = BUILDER
            .comment("\n Whether preyBlacklist should act as a whitelist rather than a blacklist")
            .define("preyBreedBlacklistIsWhitelist", true);

    public static final ModConfigSpec.DoubleValue AXOLOTL_HUNT_BREED_CHANCE = BUILDER
            .comment("\n The chance an axolotl will enter breeding mode upon killing any entity in preyBreedBlacklist")
            .defineInRange("axolotlBreedChanceAfterHunting", 0.5, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue SQUID_HUNT_BREED_CHANCE = BUILDER
            .comment("\n The chance a squid will enter breeding mode upon killing any entity in LISTNAME")
            .defineInRange("squidBreedChanceAfterHunting", 0.75, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue FISH_EAT_BREED_CHANCE = BUILDER
            .comment("\n The chance a fish will enter breeding mode upon eating its matching breeding item")
            .defineInRange("fishBreedChanceAfterScavenging", 1.0, 0.0, 1.0);

    public static final ModConfigSpec.BooleanValue ENABLE_GLOWING_POTIONS = BUILDER
            .comment("\n Enable Glowing Potion recipes")
            .define("enableGlowingPotionRecipes", true);

    public static final ModConfigSpec.BooleanValue ENABLE_INVIS_POTIONS = BUILDER
            .comment("\n Enable Invisibility Potions from Glowing Potions recipes")
            .define("enableInvisFromGlowingPotionRecipes", true);

    public static final ModConfigSpec.BooleanValue ENABLE_BLINDNESS_POTIONS = BUILDER
            .comment("\n Enable Blindness Potion recipes")
            .define("enableBlindnessPotionRecipes", true);

    public static final ModConfigSpec.BooleanValue ENABLE_WATER_BREATHING_POTIONS = BUILDER
            .comment("\n Enable Blindness Potion recipes")
            .define("enableWaterBreathingPotionRecipes", true);

    public static final ModConfigSpec.DoubleValue SQUID_PREVENT_LOVE_RADIUS = BUILDER
            .comment("\n The chance a fish will enter breeding mode upon eating its matching breeding item")
            .defineInRange("squidPreventLoveRadius", 8.0, 0.0, 64.0);

    public static final ModConfigSpec.IntValue SQUID_PREVENT_LOVE_AMOUNT = BUILDER
            .comment("\n If there are more than this number of Squids within the squidPreventLoveRadius blocks, " +
                     "\n   the Squid will not enter love mode after a successful hunt.")
            .defineInRange("squidPreventLoveLimit", 16, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue SQUID_PREVENT_LOVE_HUNT = BUILDER
            .comment("\n Whether squidPreventLoveRadius stops Squid from hunting altogether rather than just entering love mode.")
            .define("squidPreventLoveHunt", false);

    public static final ModConfigSpec.DoubleValue FISH_PREVENT_LOVE_RADIUS = BUILDER
            .comment("\n The chance a fish will enter breeding mode upon eating its matching breeding item")
            .defineInRange("fishPreventLoveRadius", 6.0, 0.0, 64.0);

    public static final ModConfigSpec.IntValue FISH_PREVENT_LOVE_AMOUNT = BUILDER
            .comment("\n If there are more than this number of Squids within the fishPreventLoveRadius blocks, " +
                     "\n   the Fish will not enter love mode after eating.")
            .defineInRange("fishPreventLoveLimit", 128, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue FISH_PREVENT_LOVE_HUNT = BUILDER
            .comment("\n Whether fishPreventLoveRadius stops Fish from chasing food altogether rather than just entering love mode.")
            .define("fishPreventLoveHunt", false);

    public static final ModConfigSpec.DoubleValue AXOLOTL_PREVENT_LOVE_RADIUS = BUILDER
            .comment("\n The chance a fish will enter breeding mode upon eating its matching breeding item")
            .defineInRange("axolotlPreventLoveRadius", 12.0, 0.0, 64.0);

    public static final ModConfigSpec.IntValue AXOLOTL_PREVENT_LOVE_AMOUNT = BUILDER
            .comment("\n If there are more than this number of Axolotls within the axolotlPreventLoveRadius blocks, " +
                     "\n   the Axolotl will not enter love mode after a successful hunt.")
            .defineInRange("axolotlPreventLoveLimit", 24, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateEntityType(final Object obj) {
        return obj instanceof String et && BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse(et));
    }

    private static boolean alwaysTrue(final Object obj) {
        return true;
    }

    private static boolean validateColor(final Object obj) {
        return obj instanceof String color && minecraftColors.contains(color);
    }
}
