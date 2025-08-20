package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.item.custom.WandItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FishEggsMod.MODID);

    @SuppressWarnings("deprecation")
    public static final DeferredItem<Item> FISH_EGGS = ITEMS.register("fish_eggs",
            () -> new FishEggsItem(new Item.Properties()
                    //.component(ModDataComponents.FE_COMP.value(), new FishEggComponents(EntityType.COD, 0, 0))
                    .food((new FoodProperties.Builder()).nutrition(1).saturationModifier(0.05F)
                            .effect(new MobEffectInstance(MobEffects.POISON, 200, 0), 0.3F)
                            .fast().build()))
            );

    public static final DeferredItem<Item> FISH_FOOD = ITEMS.register("fish_food",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> WAND = ITEMS.register("wand",
            () -> new WandItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
