package dev.cephelo.fisheggs.compat;

import com.stevekung.fishofthieves.entity.animal.*;
import com.stevekung.fishofthieves.entity.variant.*;
import com.stevekung.fishofthieves.registry.FOTRegistries;
import com.stevekung.fishofthieves.registry.variant.*;
import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class FishOfThievesCompat {
    public static int getOrderFromVariant(Entity entity) {
        switch (entity) {
            case Ancientscale fish -> {return fish.getVariant().value().customModelData();}
            case Battlegill fish -> {return fish.getVariant().value().customModelData();}
            case Devilfish fish -> {return fish.getVariant().value().customModelData();}
            case Islehopper fish -> {return fish.getVariant().value().customModelData();}
            case Plentifin fish -> {return fish.getVariant().value().customModelData();}
            case Pondie fish -> {return fish.getVariant().value().customModelData();}
            case Splashtail fish -> {return fish.getVariant().value().customModelData();}
            case Stormfish fish -> {return fish.getVariant().value().customModelData();}
            case Wildsplash fish -> {return fish.getVariant().value().customModelData();}
            case Wrecker fish -> {return fish.getVariant().value().customModelData();}
            default -> {return 0;}
        }
    }

    private static <T extends AbstractFishVariant> Holder<T> setSpecificVariant(ServerLevel serverLevel, LivingEntity livingEntity, int cmd, int cmd2, ResourceKey<net.minecraft.core.Registry<T>> variant, ResourceKey<T> defaultKey) {
        Holder<T> variant1 = getVariantHolder(serverLevel, variant, defaultKey, cmd);
        Holder<T> variant2 = getVariantHolder(serverLevel, variant, defaultKey, cmd2);
        Holder<T> variantBiome = AbstractFishVariant.getSpawnVariant(serverLevel, serverLevel.registryAccess(), variant, defaultKey, livingEntity, false);

        Holder<T> selectedVariant = variant2;
        double rnd = Math.random();
        if (rnd < Config.FISHOFTHIEVESCOMPAT_BIOMEMUTATIONCHANCE.get())
            selectedVariant = variantBiome;
        else if (rnd < ((1.0 - Config.FISHOFTHIEVESCOMPAT_BIOMEMUTATIONCHANCE.get()) / 2) + Config.FISHOFTHIEVESCOMPAT_BIOMEMUTATIONCHANCE.get())
            selectedVariant = variant1;

        if (Config.DEBUG_TROPICAL_COLORS.get()) FishEggsMod.LOGGER.info("variant1: {} | variant2: {} | variantbiome: {} | result: {}", variant1.getRegisteredName().substring(14), variant2.getRegisteredName().substring(14), variantBiome.getRegisteredName().substring(14), selectedVariant.getRegisteredName().substring(14));

        return selectedVariant;
    }

    // For retrieving variant for hatching fish
    private static <T extends AbstractFishVariant> Holder<T> getVariantHolder(ServerLevel serverLevel, ResourceKey<? extends Registry<? extends T>> registryKey, ResourceKey<T> defaultKey, int cmd) {
        Registry<T> registry = serverLevel.registryAccess().registryOrThrow(registryKey);
        Optional<Holder.Reference<T>> muha = Util.getRandomSafe(registry.holders().filter((variant) -> variant.value().customModelData() == cmd).toList(), serverLevel.getRandom());
        return (Holder)muha.orElseGet(() -> registry.getHolderOrThrow(defaultKey));
    }

    // For retrieving variant for client tooltip
    private static <T extends AbstractFishVariant> Optional<Holder.Reference<T>> getVariantString(Level level, ResourceKey<? extends Registry<? extends T>> registryKey, int cmd) {
        Registry<T> registry = level.registryAccess().registryOrThrow(registryKey);
        return Util.getRandomSafe(registry.holders().filter((variant) -> variant.value().customModelData() == cmd).toList(), level.getRandom());
    }

    public static void setVariant(LivingEntity entity, int cmd, int cmd2) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            switch (entity) {
                case Ancientscale fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.ANCIENTSCALE_VARIANT, AncientscaleVariants.ALMOND));
                case Battlegill fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.BATTLEGILL_VARIANT, BattlegillVariants.BITTERSWEET));
                case Devilfish fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.DEVILFISH_VARIANT, DevilfishVariants.ASHEN));
                case Islehopper fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.ISLEHOPPER_VARIANT, IslehopperVariants.AMETHYST));
                case Plentifin fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.PLENTIFIN_VARIANT, PlentifinVariants.AMBER));
                case Pondie fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.PONDIE_VARIANT, PondieVariants.BRIGHT));
                case Splashtail fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.SPLASHTAIL_VARIANT, SplashtailVariants.INDIGO));
                case Stormfish fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.STORMFISH_VARIANT, StormfishVariants.ANCIENT));
                case Wildsplash fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.WILDSPLASH_VARIANT, WildsplashVariants.CORAL));
                case Wrecker fish -> fish.setVariant(setSpecificVariant(serverLevel, entity, cmd, cmd2, FOTRegistries.WRECKER_VARIANT, WreckerVariants.BLACKCLOUD));
                default -> {}
            }
        }
    }

    public static MutableComponent FOTVariantString(EntityType type, int variant) {
        MutableComponent comp = Component.literal("ERROR");

        ResourceKey<? extends Registry<? extends AbstractFishVariant>> variantType = FOTRegistries.ANCIENTSCALE_VARIANT;

        if (type.getDescriptionId().contains("battlegill"))
            variantType = FOTRegistries.BATTLEGILL_VARIANT;
        else if (type.getDescriptionId().contains("devilfish"))
            variantType = FOTRegistries.DEVILFISH_VARIANT;
        else if (type.getDescriptionId().contains("islehopper"))
            variantType = FOTRegistries.ISLEHOPPER_VARIANT;
        else if (type.getDescriptionId().contains("plentifin"))
            variantType = FOTRegistries.PLENTIFIN_VARIANT;
        else if (type.getDescriptionId().contains("pondie"))
            variantType = FOTRegistries.PONDIE_VARIANT;
        else if (type.getDescriptionId().contains("splashtail"))
            variantType = FOTRegistries.SPLASHTAIL_VARIANT;
        else if (type.getDescriptionId().contains("stormfish"))
            variantType = FOTRegistries.STORMFISH_VARIANT;
        else if (type.getDescriptionId().contains("wildsplash"))
            variantType = FOTRegistries.WILDSPLASH_VARIANT;
        else if (type.getDescriptionId().contains("wrecker"))
            variantType = FOTRegistries.WRECKER_VARIANT;

        Optional<Holder.Reference<AbstractFishVariant>> variantString = getVariantString(Minecraft.getInstance().level, variantType, variant);

        if (variantString.isPresent() && variantString.get().unwrapKey().isPresent())
            comp = Component.translatable(type.getDescriptionId() + "." + variantString.get().unwrapKey().get().location().getPath());

        return comp;
    }
}
