package dev.cephelo.fisheggs.item.handler;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.item.component.FishEggComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FishHatchHandler {
    private static final Random r = new Random();

    // white, light gray, gray, black, brown, red, orange, yellow,
    // lime, green, cyan, light blue, blue, purple, magenta, pink
    private static final List<Integer> colors = Arrays.asList(0, 8, 7, 15, 12, 14, 1, 4, 5, 13, 9, 3, 11, 10, 2, 6);
    private static final List<TropicalFish.Pattern> patterns = Arrays.asList(
            // TropicalFish.Base.SMALL
            TropicalFish.Pattern.KOB, TropicalFish.Pattern.SUNSTREAK, TropicalFish.Pattern.SNOOPER,
            TropicalFish.Pattern.DASHER, TropicalFish.Pattern.BRINELY, TropicalFish.Pattern.SPOTTY,
            // TropicalFish.Base.LARGE
            TropicalFish.Pattern.STRIPEY, TropicalFish.Pattern.GLITTER, TropicalFish.Pattern.BLOCKFISH,
            TropicalFish.Pattern.BETTY, TropicalFish.Pattern.FLOPPER, TropicalFish.Pattern.CLAYFISH
    );

    private static int mutateStyle(int p) {
        return switch (p) {
            case 0 -> 4;
            case 1 -> 5;
            case 2 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            default -> 0;
        };
    }

    private static TropicalFish.Pattern mutatePattern(TropicalFish.Pattern pattern1, TropicalFish.Pattern pattern2) {
        TropicalFish.Base size = pattern1.base();
        if (size != pattern2.base() && r.nextInt(2) == 1) size = pattern2.base();

        int randomParentStyle = r.nextInt(2) == 1 ? patterns.indexOf(pattern1) : patterns.indexOf(pattern2);
        if (randomParentStyle > 5) randomParentStyle = randomParentStyle - 6;
        return patterns.get(mutateStyle(randomParentStyle) + (size == TropicalFish.Base.LARGE ? 6 : 0));
    }

    private static TropicalFish.Pattern choosePattern(int parent1, int parent2) {
        TropicalFish.Pattern pattern1 = TropicalFish.getPattern(parent1);
        TropicalFish.Pattern pattern2 = TropicalFish.getPattern(parent2);

        ArrayList<TropicalFish.Pattern> possiblePatterns = new ArrayList<>();
        possiblePatterns.add(pattern1);
        possiblePatterns.add(pattern2);

        if (Config.TROPICAL_PATTERN_MUTATION.get()) {
            TropicalFish.Pattern mutation = mutatePattern(pattern1, pattern2);
            possiblePatterns.add(mutation);
        }

        //TropicalFish.Pattern chosenPattern = possiblePatterns.get(r.nextInt(3));
        //FishEggsMod.LOGGER.info("p1: {} | p2: {} | m: {} | c: {}", pattern1, pattern2, mutation, chosenPattern);

        return possiblePatterns.get(r.nextInt(possiblePatterns.size()));//chosenPattern;
    }

    private static int wrapInt(int i) {
        if (i < 0) return 16 + i;
        if (i > 15) return i - 16;
        return i;
    }

    private static ArrayList<Integer> pushColorsInRange(int start, int end, int limit, boolean increment, boolean inner) {
        ArrayList<Integer> colorList = new ArrayList<>();

        //FishEggsMod.LOGGER.info("start: {} | end: {} | limit: {} | inc: {}", start, end, limit, increment);
        if (start == end && inner) colorList.add(start);

        if (increment) {
            for (int i = start; i <= (start + limit); i++) {
                int a = wrapInt(i);
                if (a == end && inner) break;
                colorList.add(a);
            }
        } else {
            for (int i = start; i >= (start - limit); i--) {
                int a = wrapInt(i);
                if (a == end && inner) break;
                colorList.add(a);
            }
        }

        return colorList;
    }

    private static DyeColor getRandomColorWithinRange(int variant1, int variant2, int type) {
        int color1 = colors.indexOf(variant1 >> type & 255);
        int color2 = colors.indexOf(variant2 >> type & 255);

        ArrayList<Integer> colorList = new ArrayList<>();

        int innerRange = Math.max(color1, color2) - Math.min(color1, color2);
        int crossRange = Math.min(color1, color2) - (Math.max(color1, color2) - 15);
        boolean isColor1Max = innerRange > crossRange;

        colorList.addAll(pushColorsInRange(color1, color2, Config.TROPICAL_COLOR_INNER.get(), isColor1Max, true));
        colorList.addAll(pushColorsInRange(color1, color2, Config.TROPICAL_COLOR_OUTER.get(), !isColor1Max, false));
        colorList.addAll(pushColorsInRange(color2, color1, Config.TROPICAL_COLOR_INNER.get(), !isColor1Max, true));
        colorList.addAll(pushColorsInRange(color2, color1, Config.TROPICAL_COLOR_OUTER.get(), isColor1Max, false));

        int result = colorList.get(r.nextInt(colorList.size()));
        //FishEggsMod.LOGGER.info("color1: {} | color2: {} | inv: {} | result: {} | array: {}", color1, color2, isColor1Max, result, colorList);

        return DyeColor.byId(
                colors.get(result)
        );
    }

    private static int getPattern(int variant1, int variant2) {
        TropicalFish.Pattern pattern = choosePattern(variant1, variant2);
        DyeColor baseColor = getRandomColorWithinRange(variant1, variant2, 16);
        DyeColor patternColor = getRandomColorWithinRange(variant1, variant2, 24);

        return packVariant(pattern, baseColor, patternColor);
    }

    // from TropicalFish
    static int packVariant(TropicalFish.Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
        return pattern.getPackedId() & '\uffff' | (baseColor.getId() & 255) << 16 | (patternColor.getId() & 255) << 24;
    }

    public static void spawnFish(ServerLevel level, BlockPos pos, FishEggComponents data) {
        int fishPattern = 0;
        if (Config.TROPICAL_SINGLE_PATTERN.get())
            fishPattern = getPattern(data.variant1(), data.variant2());

        for (int j = 0; j < Mth.randomBetweenInclusive(level.random, 1, 3); j++) {
            Entity thing = data.type().spawn(level, pos, MobSpawnType.SPAWN_EGG);
            if (thing != null) {
                thing.setData(FishDataAttachments.BREED_COOLDOWN, Config.HATCH_BREED_COOLDOWN_TIME.get());
                if (thing instanceof TropicalFish fish) {
                    if (!Config.TROPICAL_SINGLE_PATTERN.get())
                        fishPattern = getPattern(data.variant1(), data.variant2());
                    fish.setPackedVariant(fishPattern);
                }
            }
        }
        // play squish sound
    }
}
