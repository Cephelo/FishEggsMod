package dev.cephelo.fisheggs;

import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue FOOD_SEARCH_RANGE = BUILDER
            .comment("\n The range a fish will hunt for food")
            .defineInRange("foodSearchRange", 8, 0, 64);

    public static final ModConfigSpec.IntValue LOVE_TIME = BUILDER
            .comment("\n Ticks that a fish will be in the love state for")
            .defineInRange("inLoveTime", 600, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue REGEN_TIME = BUILDER
            .comment("\n Ticks that a fish will be given regeneration after breeding")
            .defineInRange("regenTime", 90, 0, Integer.MAX_VALUE);

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
            .define("tropicalSinglePattern", true);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
