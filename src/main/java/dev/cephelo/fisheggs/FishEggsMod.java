package dev.cephelo.fisheggs;

import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.entity.ai.goal.FishBreedGoal;
import dev.cephelo.fisheggs.entity.ai.goal.SeekFishFoodGoal;
import dev.cephelo.fisheggs.entity.ai.goal.SquidBreedGoal;
import dev.cephelo.fisheggs.entity.ai.goal.SquidHuntGoal;
import dev.cephelo.fisheggs.item.ModItems;
import dev.cephelo.fisheggs.item.component.FishEggComponents;
import dev.cephelo.fisheggs.item.component.ModDataComponents;
import dev.cephelo.fisheggs.item.component.SquidEggsComponent;
import dev.cephelo.fisheggs.item.handler.FishHatchHandler;
import dev.cephelo.fisheggs.sound.ModSounds;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FishEggsMod.MODID)
public class FishEggsMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "fisheggs";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public FishEggsMod(IEventBus modEventBus, ModContainer modContainer) {
        ModDataComponents.register(modEventBus);
        ModItems.register(modEventBus);
        FishDataAttachments.register(modEventBus);
        ModSounds.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(new ItemStack(Items.EGG), ModItems.FISH_FOOD.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.EGG), ModItems.SQUID_EGGS.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(Items.EGG), ModItems.FISH_EGGS.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.WAND);
        }
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.COOKED_SQUID_EGGS);
        }
    }

    @SubscribeEvent
    public void addAdditionalGoals(EntityJoinLevelEvent event) {
        if (!Config.DISABLE_FISH_GOALS.get() && event.getEntity() instanceof AbstractFish fish) {
            fish.targetSelector.addGoal(1, new FishBreedGoal(fish, 1.3));
            fish.targetSelector.addGoal(3, new SeekFishFoodGoal(fish, 1.3));
        }
        if (!Config.DISABLE_SQUID_GOALS.get() && event.getEntity() instanceof Squid squid) {
            squid.setData(FishDataAttachments.HAS_TARGET, false);
            squid.goalSelector.addGoal(-1, new SquidBreedGoal(squid));
            squid.goalSelector.addGoal(-1, new SquidHuntGoal(squid));
        }
    }

    // Decrement inLove and breed cooldown data attachments
    @SubscribeEvent
    public void onEntityLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof AbstractFish fish) {
            decrementAttachments(fish);
        } else if (event.getEntity() instanceof Squid squid) {
            decrementAttachments(squid);
        }
    }

    private void decrementAttachments(LivingEntity entity) {
        int inLove = entity.getData(FishDataAttachments.FISHINLOVE);
        if (inLove > 0)
            entity.setData(FishDataAttachments.FISHINLOVE, inLove - 1);

        int breedCooldown = entity.getData(FishDataAttachments.BREED_COOLDOWN);
        if (breedCooldown > 0)
            entity.setData(FishDataAttachments.BREED_COOLDOWN, breedCooldown - 1);
    }

    @SubscribeEvent
    public void addCustomTrades(VillagerTradesEvent event) {
        if (!Config.ENABLE_FISHERMAN_TRADE.get()) return;
        if (event.getType() == VillagerProfession.FISHERMAN) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ModItems.FISH_FOOD.get(), 12),
                    new ItemStack(Items.EMERALD, 1), 8, 5, 0.05f
            ));

            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ModItems.FISH_EGGS.get(), 3),
                    new ItemStack(Items.EMERALD, 1), 8, 25, 0.05f
            ));

            trades.get(5).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ModItems.SQUID_EGGS.get(), 1),
                    new ItemStack(Items.EMERALD, 1), 8, 30, 0.05f
            ));
        }

        if (event.getType() == VillagerProfession.BUTCHER) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ModItems.COOKED_SQUID_EGGS.get(), 2),
                    new ItemStack(Items.EMERALD, 4), 8, 30, 0.05f
            ));
        }
    }

    @SubscribeEvent
    public void addWanderingTrades(WandererTradesEvent event) {
        if (!Config.ENABLE_WANDERING_TRADE.get()) return;
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        ItemStack clownDotty = new ItemStack(ModItems.FISH_EGGS.get(), 1);
        clownDotty.set(ModDataComponents.FE_COMP.value(), new FishEggComponents(EntityType.TROPICAL_FISH,
                FishHatchHandler.packVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE),
                FishHatchHandler.packVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW)));

        genericTrades.add((entity, randomSource) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 2), clownDotty, 3, 10, 0.05f
        ));


        ItemStack cichlids = new ItemStack(ModItems.FISH_EGGS.get(), 1);
        cichlids.set(ModDataComponents.FE_COMP.value(), new FishEggComponents(EntityType.TROPICAL_FISH,
                FishHatchHandler.packVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY),
                FishHatchHandler.packVariant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE)));

        genericTrades.add((entity, randomSource) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 2), cichlids, 3, 10, 0.05f
        ));


        ItemStack cottonYeParrot = new ItemStack(ModItems.FISH_EGGS.get(), 1);
        cottonYeParrot.set(ModDataComponents.FE_COMP.value(), new FishEggComponents(EntityType.TROPICAL_FISH,
                FishHatchHandler.packVariant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE),
                FishHatchHandler.packVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW)));

        genericTrades.add((entity, randomSource) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 2), cottonYeParrot, 3, 10, 0.05f
        ));


//        ItemStack blueSnapper = new ItemStack(ModItems.FISH_EGGS.get(), 1);
//        blueSnapper.set(ModDataComponents.FE_COMP.value(), new FishEggComponents(EntityType.TROPICAL_FISH,
//                FishHatchHandler.packVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE),
//                FishHatchHandler.packVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED)));
//
//        genericTrades.add((entity, randomSource) -> new MerchantOffer(
//                new ItemCost(Items.EMERALD, 2), blueSnapper, 3, 10, 0.05f
//        ));


        ItemStack tenebris = new ItemStack(ModItems.FISH_EGGS.get(), 1);
        tenebris.set(ModDataComponents.FE_COMP.value(), new FishEggComponents(EntityType.TROPICAL_FISH,
                FishHatchHandler.packVariant(TropicalFish.Pattern.SPOTTY, DyeColor.BLACK, DyeColor.BLACK),
                FishHatchHandler.packVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.BLACK, DyeColor.BLACK)));

        rareTrades.add((entity, randomSource) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 12), tenebris, 1, 10, 0.05f
        ));

        ItemStack squideggs = new ItemStack(ModItems.SQUID_EGGS.get(), 1);
        squideggs.set(ModDataComponents.SE_COMP.value(), new SquidEggsComponent(EntityType.GLOW_SQUID));

        rareTrades.add((entity, randomSource) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 12), squideggs, 2, 10, 0.05f
        ));


    }
}