package dev.cephelo.fisheggs;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final List<String> minecraftColors = Arrays.asList("white", "orange", "magenta", "light_blue",
            "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");

    public static final ModConfigSpec.BooleanValue
            TROPICAL_SINGLE_PATTERN,
            FISH_BLACKLIST_IS_WHITELIST,
            ENABLE_FISHERMAN_TRADE,
            ENABLE_WANDERING_TRADE,
            SHOW_TYPE_TOOLTIP,
            SHOW_VARIANT_TOOLTIP,
            HAS_BRED_DESPAWN,
            HATCHED_CAN_DESPAWN,
            HUNT_BLACKLIST_IS_WHITELIST,
            CANHUNT_BLACKLIST_IS_WHITELIST,
            SQUID_HAS_BRED_DESPAWN,
            SQUID_HATCHED_CAN_DESPAWN,
            DISABLE_FISH_HUNTING,
            DISABLE_FISH_BREEDING,
            DISABLE_SQUID_HUNTING,
            DISABLE_SQUID_BREEDING,
            SQUID_SHOW_TYPE_TOOLTIP,
            FISH_EGGS_NEED_WATER,
            SQUID_EGGS_NEED_WATER,
            AXOLOTL_BREED_BLACKLIST_IS_WHITELIST,
            ENABLE_GLOWING_POTIONS,
            ENABLE_INVIS_POTIONS,
            ENABLE_BLINDNESS_POTIONS,
            ENABLE_WATER_BREATHING_POTIONS,
            SQUID_PREVENT_LOVE_HUNT,
            FISH_PREVENT_LOVE_HUNT,
            SQUID_BREED_BLACKLIST_IS_WHITELIST,
            FISH_HANDFED_BLACKLIST_IS_WHITELIST,
            FISH_BREED_BLACKLIST_IS_WHITELIST,
            DEBUG_TROPICAL_COLORS;

    public static final ModConfigSpec.IntValue
            TROPICAL_PATTERN_MUTATION,
            LOVE_TIME,
            REGEN_TIME,
            CALM_DOWN_TIME,
            CALM_DOWN_TIME_FAIL,
            BREED_COOLDOWN_TIME,
            HATCH_BREED_COOLDOWN_TIME,
            HATCH_TIME,
            TROPICAL_COLOR_INNER,
            TROPICAL_COLOR_OUTER,
            MAX_FISH_FROM_EGGS,
            FISH_BREEDING_XP,
            SQUID_HUNT_DAMAGE,
            SQUID_LOVE_TIME,
            SQUID_REGEN_TIME,
            SQUID_CALM_DOWN_TIME,
            SQUID_CALM_DOWN_TIME_FAIL,
            SQUID_BREED_COOLDOWN_TIME,
            SQUID_HATCH_BREED_COOLDOWN_TIME,
            SQUID_HATCH_TIME,
            SQUID_BREEDING_XP,
            MAX_SQUID_FROM_EGGS,
            SQUID_PREVENT_LOVE_AMOUNT,
            FISH_PREVENT_LOVE_AMOUNT,
            CONSUME_PREY,
            AXOLOTL_PREVENT_LOVE_AMOUNT;

    public static final ModConfigSpec.DoubleValue
            FOOD_SEARCH_RANGE,
            MATE_SEARCH_RANGE,
            DIST_FOOD,
            DIST_BREED,
            HUNT_SEARCH_RANGE,
            SQUID_MATE_SEARCH_RANGE,
            SQUID_DIST_HUNT,
            SQUID_DIST_BREED,
            AXOLOTL_HUNT_BREED_CHANCE,
            SQUID_HUNT_BREED_CHANCE,
            FISH_EAT_BREED_CHANCE,
            SQUID_PREVENT_LOVE_RADIUS,
            FISH_PREVENT_LOVE_RADIUS,
            AXOLOTL_PREVENT_LOVE_RADIUS,
            WAND_BIGUSE_RADIUS;

    public static final ModConfigSpec.ConfigValue<List<? extends String>>
            FISH_IDS,
            FOOD_TAGS,
            FISH_BLACKLIST,
            HUNT_BLACKLIST,
            CANHUNT_BLACKLIST,
            COLOR_LIST,
            SQUID_IDS,
            SQUID_FOOD_TAGS,
            FISH_HANDFED_BLACKLIST,
            AXOLOTL_BREED_BLACKLIST,
            SQUID_BREED_BLACKLIST,
            FISH_BREED_BLACKLIST;

    static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BUILDER.comment(" IF YOU HAVE ANY SUGGESTIONS FOR MORE CONFIG OPTIONS, LET ME KNOW IN THE MOD PAGE'S COMMENTS, OR IN MY DISCORD! - Cephelo" +
                "\n Aquatic Breeding Config\n");

        // FISH EGGS OPTIONS
        BUILDER.push("Fish Eggs & Hatching");

        HATCH_TIME = BUILDER
                .comment(" Ticks that Fish Eggs will take to hatch")
                .defineInRange("fishHatchTime", 6000, 1, Integer.MAX_VALUE);

        MAX_FISH_FROM_EGGS = BUILDER
                .comment("\n The maximum amount of fish that can hatch from a single Fish Eggs item")
                .defineInRange("maxFishFromEggs", 3, 1, 16);

        FISH_EGGS_NEED_WATER = BUILDER
                .comment("\n If true, Fish Eggs can only hatch while inside allowed fluids (water by default, see fluid tag fisheggs:allowed_hatch_fluids)")
                .define("fishEggsNeedWater", true);

        HATCH_BREED_COOLDOWN_TIME = BUILDER
                .comment("\n Ticks that a fish will be unable to breed or hunt food after hatching")
                .defineInRange("fishHatchBreedCooldownTime", 12000, 0, Integer.MAX_VALUE);

        HATCHED_CAN_DESPAWN = BUILDER
                .comment("\n Whether fish can despawn if they hatch from Fish Eggs.  Not retroactive.")
                .define("fishHatchedCanDespawn", false);

        SHOW_TYPE_TOOLTIP = BUILDER
                .comment("\n Whether the Fish Eggs item should display the entity type of the parents")
                .define("fishShowEntityTypeTooltip", true);

        SHOW_VARIANT_TOOLTIP = BUILDER
                .comment("\n Whether the Fish Eggs item should display the colors and patterns of the parents")
                .define("showVariantTooltip", true);

        BUILDER.pop();


        // SQUID EGGS OPTIONS
        BUILDER.push("Squid Eggs & Hatching");

        SQUID_HATCH_TIME = BUILDER
                .comment(" Ticks that Squid Eggs will take to hatch")
                .defineInRange("squidHatchTime", 6000, 1, Integer.MAX_VALUE);

        MAX_SQUID_FROM_EGGS = BUILDER
                .comment("\n The maximum amount of squid that can hatch from a single Squid Eggs item")
                .defineInRange("maxSquidFromEggs", 2, 1, 16);

        SQUID_EGGS_NEED_WATER = BUILDER
                .comment("\n If true, Squid Eggs can only hatch while inside allowed fluids (water by default, see fluid tag fisheggs:allowed_hatch_fluids)")
                .define("squidEggsNeedWater", true);

        SQUID_HATCH_BREED_COOLDOWN_TIME = BUILDER
                .comment("\n Ticks that a Squid will be unable to breed or hunt food after hatching (baby squids don't exist in 1.21.1)")
                .defineInRange("squidHatchBreedCooldownTime", 24000, 0, Integer.MAX_VALUE);

        SQUID_HATCHED_CAN_DESPAWN = BUILDER
                .comment("\n Whether squid can despawn if they hatch from Squid Eggs.  Not retroactive.")
                .define("squidHatchedCanDespawn", false);

        SQUID_SHOW_TYPE_TOOLTIP = BUILDER
                .comment("\n Whether the Squid Eggs item should display the entity type of the parents")
                .define("squidShowEntityTypeTooltip", true);

        BUILDER.pop();


        // FISH HUNTING BEHAVIOR
        BUILDER.push("Fish Hunting Behavior");

        DISABLE_FISH_HUNTING = BUILDER
                .comment(" Completely disable Fish chasing matching food item entities")
                .define("disableFishEatChasingCompletely", false);

        FISH_BLACKLIST = BUILDER
                .comment("\n List of Entity IDs - Fish that should not chase food item entities")
                .defineListAllowEmpty("fishBlacklist", List.of("minecraft:tadpole"), () -> "", Config::validateEntityType);

        FISH_BLACKLIST_IS_WHITELIST = BUILDER
                .comment("\n Whether fishBlacklist should act as a whitelist rather than a blacklist")
                .define("fishBlacklistIsWhitelist", false);

        FOOD_SEARCH_RANGE = BUILDER
                .comment("\n The range a fish can see matching food item entities from")
                .defineInRange("fishFoodSearchRange", 8.0, 0.0, 64.0);

        DIST_FOOD = BUILDER
                .comment("\n The maximum squared distance a fish can consume a food item entity")
                .defineInRange("fishFoodEatRangeSqr", 1.1, 0.0, 64.0);

        CALM_DOWN_TIME = BUILDER
                .comment("\n Ticks that a fish will ignore food after successfully eating")
                .defineInRange("fishCalmdownTimeSuccess", 900, 0, Integer.MAX_VALUE);

        CALM_DOWN_TIME_FAIL = BUILDER
                .comment("\n Ticks that a fish will ignore food after losing sight of their previous food target")
                .defineInRange("fishCalmdownTimeFail", 60, 0, Integer.MAX_VALUE);

        LOVE_TIME = BUILDER
                .comment("\n Ticks that a fish will be in the love state for after eating or being fed")
                .defineInRange("fishInLoveTime", 600, 1, Integer.MAX_VALUE);

        FISH_EAT_BREED_CHANCE = BUILDER
                .comment("\n The chance a fish will enter breeding mode upon eating a matching food item entity.  Does not affect hand-feeding.")
                .defineInRange("fishBreedChanceAfterScavenging", 1.0, 0.0, 1.0);

        FISH_PREVENT_LOVE_RADIUS = BUILDER
                .comment("\n If there are more than fishPreventLoveLimit of Squids within this radius of blocks, " +
                        "\n   the Fish will not enter love mode after eating.")
                .defineInRange("fishPreventLoveRadius", 8.0, 0.0, 64.0);

        FISH_PREVENT_LOVE_AMOUNT = BUILDER
                .comment("\n If there are more than this number of Squids within the fishPreventLoveRadius blocks, " +
                        "\n   the Fish will not enter love mode after eating.")
                .defineInRange("fishPreventLoveLimit", 128, 0, Integer.MAX_VALUE);

        FISH_PREVENT_LOVE_HUNT = BUILDER
                .comment("\n Whether fishPreventLoveRadius stops Fish from chasing food altogether rather than just entering love mode.")
                .define("fishPreventLoveHunt", false);

        FISH_IDS = BUILDER
                .comment("\n List of Entity IDs - each entry corresponds with the entry of fishFood_itemTags with the same index." +
                        "\n   If entry index is greater than fishFood_itemTags length, will retrieve last entry in fishFood_itemTags." +
                        "\n   Must be instance of AbstractFish class.  If an entity ID is not defined here, the default tag fisheggs:fish_food will be used." +
                        "\n   If this list is empty, the item tag fisheggs:fish_food will be used by default.")
                .defineListAllowEmpty("fishFood_fishIDs", List.of("minecraft:cod", "minecraft:salmon"), () -> "", Config::validateEntityType);

        FOOD_TAGS = BUILDER
                .comment("\n List of item tags - each entry corresponds with the entry of fishFood_fishIDs with the same index." +
                        "\n   If this list is empty, the item tag fisheggs:fish_food will be used by default.")
                .defineListAllowEmpty("fishFood_itemTags", List.of("fisheggs:fish_food"), () -> "", Config::alwaysTrue);

        FISH_HANDFED_BLACKLIST = BUILDER
                .comment("\n List of Entity IDs - list of Fish that cannot be handfed.")
                .defineListAllowEmpty("fishCannotBeHandfed", List.of(), () -> "", Config::validateEntityType);

        FISH_HANDFED_BLACKLIST_IS_WHITELIST = BUILDER
                .comment("\n Whether fishCannotBeHandfed should act as a whitelist rather than a blacklist")
                .define("fishCannotBeHandfedIsWhitelist", true);

        BUILDER.pop();


        // FISH BREEDING BEHAVIOR
        BUILDER.push("Fish Breeding Behavior");

        DISABLE_FISH_BREEDING = BUILDER
                .comment(" Completely disable Fish breeding behavior")
                .define("disableFishBreedingCompletely", false);

        FISH_BREED_BLACKLIST = BUILDER
                .comment("\n List of Entity IDs - Fish that cannot breed")
                .defineListAllowEmpty("fishBreedBlacklist", List.of("minecraft:tadpole"), () -> "", Config::validateEntityType);

        FISH_BREED_BLACKLIST_IS_WHITELIST = BUILDER
                .comment("\n Whether fishBreedBlacklist should act as a whitelist rather than a blacklist")
                .define("fishBreedBlacklistIsWhitelist", false);

        MATE_SEARCH_RANGE = BUILDER
                .comment("\n The range a fish will hunt for a breeding partner")
                .defineInRange("fishPartnerSearchRange", 8.0, 0.0, 64.0);

        DIST_BREED = BUILDER
                .comment("\n The maximum squared distance a fish can breed with their partner")
                .defineInRange("fishPartnerMateRangeSqr", 1.1, 0.0, 64.0);

        REGEN_TIME = BUILDER
                .comment("\n Ticks that a fish will be given regeneration after breeding")
                .defineInRange("fishRegenTime", 100, 0, Integer.MAX_VALUE);

        BREED_COOLDOWN_TIME = BUILDER
                .comment("\n Ticks that a fish will be unable to breed or hunt food after breeding")
                .defineInRange("fishBreedCooldownTime", 3600, 0, Integer.MAX_VALUE);

        FISH_BREEDING_XP = BUILDER
                .comment("\n Maximum value of the XP orb produced when fish breed; 0 to disable")
                .defineInRange("fishBreedingMaxXP", 4, 0, Integer.MAX_VALUE);

        HAS_BRED_DESPAWN = BUILDER
                .comment("\n Whether fish can despawn if they've bred.  Not retroactive.")
                .define("fishParentsCanDespawn", false);

        BUILDER.pop();


        // SQUID HUNTING BEHAVIOR
        BUILDER.push("Squid Hunting Behavior");

        DISABLE_SQUID_HUNTING = BUILDER
                .comment(" Completely disable Squids hunting for prey.")
                .define("disableSquidHuntingCompletely", false);

        CANHUNT_BLACKLIST = BUILDER
                .comment("\n List of Entity IDs - Squids that cannot hunt for prey")
                .defineListAllowEmpty("squidHuntBlacklist", List.of(), () -> "", Config::validateEntityType);

        CANHUNT_BLACKLIST_IS_WHITELIST = BUILDER
                .comment("\n Whether squidHuntBlacklist should act as a whitelist rather than a blacklist")
                .define("squidHuntBlacklistIsWhitelist", false);

        HUNT_BLACKLIST = BUILDER
                .comment("\n List of Entity IDs - entities that squids will not hunt")
                .defineListAllowEmpty("squidPreyBlacklist", List.of("minecraft:cod", "minecraft:salmon", "minecraft:tropical_fish"), () -> "", Config::validateEntityType);

        HUNT_BLACKLIST_IS_WHITELIST = BUILDER
                .comment("\n Whether squidPreyBlacklist should act as a whitelist rather than a blacklist")
                .define("squidPreyBlacklistIsWhitelist", true);

        HUNT_SEARCH_RANGE = BUILDER
                .comment("\n The range a squid will hunt for prey")
                .defineInRange("squidHuntSearchRange", 12.0, 0.0, 64.0);

        SQUID_DIST_HUNT = BUILDER
                .comment("\n The maximum squared distance away a squid can hit prey from")
                .defineInRange("squidPreyHitRangeSqr", 1.5, 0.0, 64.0);

        SQUID_HUNT_DAMAGE = BUILDER
                .comment("\n The amount of damage a squid does when hitting its prey." +
                        "\n   If squidConsumeWeakPrey is not 0, entities with squidConsumeWeakPrey HP (or less) remaining will be consumed entirely.")
                .defineInRange("squidHuntDamage", 2, 1, Integer.MAX_VALUE);

        CONSUME_PREY = BUILDER
                .comment("\n Prey with this amount of HP (or less) remaining will be consumed entirely by the Squid, thus dropping no items or XP." +
                        "\n   Set to 0 for Squid to kill prey normally." +
                        "\n   Note that if squidHuntDamage is equal to or higher than the prey's remaining HP when hit, it will die normally.")
                .defineInRange("squidConsumeWeakPrey", 1, 0, Integer.MAX_VALUE);

        SQUID_CALM_DOWN_TIME = BUILDER
                .comment("\n Ticks that a squid will ignore food after a successful hunt")
                .defineInRange("squidCalmdownTimeSuccess", 2400, 0, Integer.MAX_VALUE);

        SQUID_CALM_DOWN_TIME_FAIL = BUILDER
                .comment("\n Ticks that a squid will ignore food after losing sight of their prey")
                .defineInRange("squidCalmdownTimeFail", 600, 0, Integer.MAX_VALUE);

        SQUID_LOVE_TIME = BUILDER
                .comment("\n Ticks that a squid will be in the love state for after eating")
                .defineInRange("squidInLoveTime", 1200, 1, Integer.MAX_VALUE);

        SQUID_HUNT_BREED_CHANCE = BUILDER
                .comment("\n The chance a squid will enter love mode upon consuming/killing its prey")
                .defineInRange("squidBreedChanceAfterHunting", 0.75, 0.0, 1.0);

        SQUID_PREVENT_LOVE_RADIUS = BUILDER
                .comment("\n If there are more than squidPreventLoveLimit of Squid within this radius of blocks," +
                        "\n   the Squid will not enter love mode after a successful hunt.")
                .defineInRange("squidPreventLoveRadius", 10.0, 0.0, 64.0);

        SQUID_PREVENT_LOVE_AMOUNT = BUILDER
                .comment("\n If there are more than this number of Squids within the squidPreventLoveRadius blocks," +
                        "\n   the Squid will not enter love mode after a successful hunt.")
                .defineInRange("squidPreventLoveLimit", 16, 0, Integer.MAX_VALUE);

        SQUID_PREVENT_LOVE_HUNT = BUILDER
                .comment("\n Whether squidPreventLoveRadius stops Squid from hunting altogether rather than just entering love mode.")
                .define("squidPreventLoveHunt", false);

        SQUID_IDS = BUILDER
                .comment("\n List of Entity IDs - each entry corresponds with the entry of squidFood_itemTags with the same index." +
                        "\n   If entry index is greater than squidFood_itemTags length, will retrieve last entry in squidFood_itemTags." +
                        "\n   Must be instance of Squid class.  If an entity ID is not defined here, it cannot be hand-fed." +
                        "\n   THIS ONLY APPLIES TO HAND FEEDING SQUID.")
                .defineListAllowEmpty("squidFood_squidIDs", List.of("minecraft:squid", "minecraft:glow_squid"), () -> "", Config::validateEntityType);

        SQUID_FOOD_TAGS = BUILDER
                .comment("\n List of item tags - each entry corresponds with the entry of squidFood_squidIDs with the same index." +
                        "\n   If this list is empty and squidFood_squidIDs is not, the tag fisheggs:squid_food will be used by default." +
                        "\n   THIS ONLY APPLIES TO HAND FEEDING SQUID.")
                .defineListAllowEmpty("squidFood_itemTags", List.of("fisheggs:squid_food"), () -> "", Config::alwaysTrue);

        BUILDER.pop();


        // SQUID BREEDING BEHAVIOR
        BUILDER.push("Squid Breeding Behavior");

        DISABLE_SQUID_BREEDING = BUILDER
                .comment(" Completely disable Squids breeding behavior.")
                .define("disableSquidBreedingCompletely", false);

        SQUID_BREED_BLACKLIST = BUILDER
                .comment("\n List of Entity IDs - Squids that cannot breed")
                .defineListAllowEmpty("squidBreedBlacklist", List.of(), () -> "", Config::validateEntityType);

        SQUID_BREED_BLACKLIST_IS_WHITELIST = BUILDER
                .comment("\n Whether squidBreedBlacklist should act as a whitelist rather than a blacklist")
                .define("squidBreedBlacklistIsWhitelist", false);

        SQUID_MATE_SEARCH_RANGE = BUILDER
                .comment("\n The range a squid will hunt for a breeding partner")
                .defineInRange("squidPartnerSearchRange", 16.0, 0.0, 64.0);

        SQUID_DIST_BREED = BUILDER
                .comment("\n The maximum squared distance a fish can breed with their partner")
                .defineInRange("squidPartnerMateRangeSqr", 1.5, 0.0, 64.0);

        SQUID_REGEN_TIME = BUILDER
                .comment("\n Ticks that a squid will be given regeneration after breeding")
                .defineInRange("squidRegenTime", 100, 0, Integer.MAX_VALUE);

        SQUID_BREED_COOLDOWN_TIME = BUILDER
                .comment("\n Ticks that a squid will be unable to breed or hunt food after breeding")
                .defineInRange("squidBreedCooldownTime", 6000, 0, Integer.MAX_VALUE);

        SQUID_BREEDING_XP = BUILDER
                .comment("\n Maximum value of the XP orb produced when squid breed; 0 to disable")
                .defineInRange("squidBreedingMaxXP", 8, 0, Integer.MAX_VALUE);

        SQUID_HAS_BRED_DESPAWN = BUILDER
                .comment("\n Whether squid can despawn if they've bred.  Not retroactive.")
                .define("squidParentsCanDespawn", false);

        BUILDER.pop();


        // AXOLOTL AUTO-BREEDING BEHAVIOR
        BUILDER.push("Axolotl Auto-Breeding Behavior");

        AXOLOTL_BREED_BLACKLIST = BUILDER
                .comment(" List of Entity IDs - Axolotls cannot go into love mode upon killing one of these (blacklist behavior)" +
                        "\n   Axolotls will go into love mode upon killing one of these (whitelist behavior)")
                .defineListAllowEmpty("axolotlPreyBreedBlacklist", List.of("minecraft:tropical_fish"), () -> "", Config::validateEntityType);

        AXOLOTL_BREED_BLACKLIST_IS_WHITELIST = BUILDER
                .comment("\n Whether axolotlPreyBreedBlacklist should act as a whitelist rather than a blacklist")
                .define("axolotlPreyBreedBlacklistIsWhitelist", true);

        AXOLOTL_HUNT_BREED_CHANCE = BUILDER
                .comment("\n The chance an Axolotl will enter breeding mode upon killing a matching prey entity, as defined above")
                .defineInRange("axolotlBreedChanceAfterHunting", 0.5, 0.0, 1.0);

        AXOLOTL_PREVENT_LOVE_RADIUS = BUILDER
                .comment("\n The chance an Axolotl will enter breeding mode upon eating its matching breeding item")
                .defineInRange("axolotlPreventLoveRadius", 12.0, 0.0, 64.0);

        AXOLOTL_PREVENT_LOVE_AMOUNT = BUILDER
                .comment("\n If there are more than this number of Axolotls within the axolotlPreventLoveRadius blocks, " +
                        "\n   the Axolotl will not enter love mode after a successful hunt.")
                .defineInRange("axolotlPreventLoveLimit", 24, 0, Integer.MAX_VALUE);

        BUILDER.pop();


        // TROPICAL FISH GENETICS
        BUILDER.push("Tropical Fish Genetics");

        TROPICAL_SINGLE_PATTERN = BUILDER
                .comment(" Whether tropical fish that hatch out of the same egg clutch should be identical")
                .define("tropicalSinglePattern", false);

        COLOR_LIST = BUILDER
                .comment("\n Color list used in Tropical Fish color mutations.  All strings must match the 16 minecraft colors, or default will be used." +
                        "\n  Default: [\"white\", \"light_gray\", \"gray\", \"black\", \"brown\", " +
                        "\"red\", \"orange\", \"yellow\", \"lime\", \"green\", \"cyan\", \"light_blue\", \"blue\", \"purple\", \"magenta\", \"pink\"]")
                .defineListAllowEmpty("colorMutationList", List.of("white", "light_gray", "gray", "black", "brown",
                        "red", "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink"), () -> "", Config::validateColor);

        TROPICAL_COLOR_INNER = BUILDER
                .comment("\n The number of colors that will be added to the list of possible colors for a hatchling's base color or pattern color" +
                        "\n + From (and including) the parent's starting color, moving towards the other parent's starting color (will take shortest distance, wrapping the color array if necessary)")
                .defineInRange("tropicalColorInner", 2, 0, 15);

        TROPICAL_COLOR_OUTER = BUILDER
                .comment("\n The number of colors that will be added to the list of possible colors for a hatchling's base color or pattern color" +
                        "\n + From the parent's starting color, moving away from other parent's starting color (will take shortest distance, wrapping the color array if necessary)")
                .defineInRange("tropicalColorOuter", 1, 0, 15);

        TROPICAL_PATTERN_MUTATION = BUILDER
                .comment("\n Number of mutations to add to the hatching's possible pattern list (mutations are based on that of one of their parents)")
                .defineInRange("tropicalPatternMutation", 1, 0, 16);

        BUILDER.pop();


        // MISCELLANEOUS
        BUILDER.push("Miscellaneous");

        ENABLE_FISHERMAN_TRADE = BUILDER
                .comment(" Enable/Disable all villager trades added by this mod")
                .define("enableVillagerTrades", true);

        ENABLE_WANDERING_TRADE = BUILDER
                .comment("\n Enable/Disable all wandering trader trades added by this mod")
                .define("enableWanderingTrades", true);

        ENABLE_GLOWING_POTIONS = BUILDER
                .comment("\n Enable Glowing Potion recipes (night vision + glow ink sac)")
                .define("enableGlowingPotionRecipes", true);

        ENABLE_INVIS_POTIONS = BUILDER
                .comment("\n Enable Invisibility Potions from Glowing Potions recipes (glowing + fermented spider eye)")
                .define("enableInvisFromGlowingPotionRecipes", true);

        ENABLE_BLINDNESS_POTIONS = BUILDER
                .comment("\n Enable Blindness Potion recipes (night vision + ink sac")
                .define("enableBlindnessPotionRecipes", true);

        ENABLE_WATER_BREATHING_POTIONS = BUILDER
                .comment("\n Enable Water Breathing Potion recipe (awkward + squid eggs)")
                .define("enableWaterBreathingPotionRecipe", true);

        WAND_BIGUSE_RADIUS = BUILDER
                .comment("\n Radius of effect for the Dev Wand's crouch uses")
                .defineInRange("wandCrouchUseRadius", 12.0, 0.0, 64.0);

        DEBUG_TROPICAL_COLORS = BUILDER
                .comment("\n Whether Fish Eggs should output the genetic color data of hatching tropical fish to latest.log (for testing)")
                .define("debugTropicalColorsToLog", false);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

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
