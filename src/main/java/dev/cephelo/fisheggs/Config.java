package dev.cephelo.fisheggs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            .defineInRange("calmdownTime", 30, 0, Integer.MAX_VALUE);

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
                    "\n   If entry index is greater than fishFood_itemTags length, will retrieve last entry in fishFood_itemTags. \n" +
                    "\n   Must be instance of AbstractFish.  If an entity ID is not defined here, the default tag fisheggs:fish_food will be used." +
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
            .comment("\n The maximum amount of fish that can hatch from a single Fish Roe item")
            .defineInRange("maxFishFromEggs", 3, 1, 16);

    public static final ModConfigSpec.BooleanValue ENABLE_FISHERMAN_TRADE = BUILDER
            .comment("\n Whether fishBlacklist should act as a whitelist rather than a blacklist")
            .define("enableFishermanTrades", true);

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
            .comment("\n Whether (bucketable) fish can despawn if they've bred.  Existing fish will not despawn if set to true.")
            .define("parentsCanDespawn", false);

    public static final ModConfigSpec.BooleanValue HATCHED_CAN_DESPAWN = BUILDER
            .comment("\n Whether (bucketable) fish can despawn if they hatch from Fish Eggs.  Existing fish will not despawn if set to true.")
            .define("hatchedCanDespawn", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> COLOR_LIST = BUILDER
            .comment("\n Color list used in Tropical Fish color mutations.  All strings must match the 16 minecraft colors, or default will be used." +
                    "\n  Default: [\"white\", \"light_gray\", \"gray\", \"black\", \"brown\", " +
                    "\"red\", \"orange\", \"yellow\", \"lime\", \"green\", \"cyan\", \"light_blue\", \"blue\", \"purple\", \"magenta\", \"pink\"]")
            .defineListAllowEmpty("colorMutationList", List.of("white", "light_gray", "gray", "black", "brown",
                    "red", "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink"), () -> "", Config::validateColor);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateEntityType(final Object obj) {
        return obj instanceof String et && BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse(et));
    }

    private static boolean alwaysTrue(final Object obj) {
        return true;
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    private static boolean validateColor(final Object obj) {
        return obj instanceof String color && minecraftColors.contains(color);
    }
}
