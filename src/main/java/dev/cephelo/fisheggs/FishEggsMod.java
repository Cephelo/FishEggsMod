package dev.cephelo.fisheggs;

import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.entity.ai.goal.FishBreedGoal;
import dev.cephelo.fisheggs.entity.ai.goal.SeekFishFoodGoal;
import dev.cephelo.fisheggs.item.ModItems;
import dev.cephelo.fisheggs.item.component.ModDataComponents;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.world.entity.animal.AbstractFish;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

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
            event.accept(ModItems.FISH_EGGS);
            event.accept(ModItems.FISH_FOOD);
        }
    }

    @SubscribeEvent
    public void addAdditionalGoals(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractFish fish) {
            fish.targetSelector.addGoal(1, new FishBreedGoal(fish, 1.25));
            fish.targetSelector.addGoal(3, new SeekFishFoodGoal(fish, 1.25));
        }
    }

    @SubscribeEvent
    public void onEntityLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof AbstractFish fish) {
            int inLove = fish.getData(FishDataAttachments.FISHINLOVE);
            if (inLove > 0)
                fish.setData(FishDataAttachments.FISHINLOVE, inLove - 1);

            int breedCooldown = fish.getData(FishDataAttachments.BREED_COOLDOWN);
            if (breedCooldown > 0)
                fish.setData(FishDataAttachments.BREED_COOLDOWN, breedCooldown - 1);

        }
    }
}
