package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.item.custom.WandItem;
import dev.cephelo.fisheggs.item.squid.SquidEggsItem;
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

    public static final DeferredItem<Item> SQUID_EGGS = ITEMS.register("squid_eggs",
            () -> new SquidEggsItem(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F)
                            .effect(new MobEffectInstance(MobEffects.POISON, 200, 1), 0.3F)
                            .build()))
    );

    public static final DeferredItem<Item> COOKED_SQUID_EGGS = ITEMS.register("cooked_squid_eggs",
            () -> new Item(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.5F)
                            .build()))
    );

    public static final DeferredItem<Item> SQUID_TENTACLE = ITEMS.register("squid_tentacle",
            () -> new Item(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.2F).build()))
    );

    public static final DeferredItem<Item> GLOW_SQUID_TENTACLE = ITEMS.register("glow_squid_tentacle",
            () -> new Item(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.2F)
                            .effect(new MobEffectInstance(MobEffects.GLOWING, 300, 0), 1.0F)
                            .build()))
    );

    public static final DeferredItem<Item> COOKED_SQUID_TENTACLE = ITEMS.register("cooked_squid_tentacle",
            () -> new Item(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(5).saturationModifier(0.6F).build()))
    );

    public static final DeferredItem<Item> CALAMARI_SUPREME = ITEMS.register("calamari_supreme",
            () -> new Item(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(12).saturationModifier(1.6F)
                            .effect(new MobEffectInstance(MobEffects.LUCK, 1800, 0), 1.0F)
                            .build()))
    );

    public static final DeferredItem<Item> WAND = ITEMS.register("wand",
            () -> new WandItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
