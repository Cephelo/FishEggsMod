package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.item.custom.EffectTooltipItem;
import dev.cephelo.fisheggs.item.custom.WandItem;
import dev.cephelo.fisheggs.item.squid.SquidEggsItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FishEggsMod.MODID);

    public static final DeferredItem<Item> FISH_EGGS = ITEMS.register("fish_eggs",
            () -> new FishEggsItem(new Item.Properties()
                    //.component(ModDataComponents.FE_COMP.value(), new FishEggComponents(EntityType.COD, 0, 0))
                    .food((new FoodProperties.Builder()).nutrition(1).saturationModifier(0.05F)
                            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 0), 0.4F)
                            .fast().build()))
            );

    public static final DeferredItem<Item> FISH_FOOD = ITEMS.register("fish_food",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SQUID_EGGS = ITEMS.register("squid_eggs",
            () -> new SquidEggsItem(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F)
                            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 1), 0.4F)
                            .build()))
    );

    public static final DeferredItem<Item> COOKED_SQUID_EGGS = ITEMS.register("cooked_squid_eggs",
            () -> new Item(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.5F)
                            .build()))
    );

    public static final DeferredItem<Item> SQUID_TENTACLE = ITEMS.register("squid_tentacle",
            () -> new Item(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.2F).build()))
    );

    static MobEffectInstance glowing = new MobEffectInstance(MobEffects.GLOWING, 300, 0);
    public static final DeferredItem<Item> GLOW_SQUID_TENTACLE = ITEMS.register("glow_squid_tentacle",
            () -> new EffectTooltipItem(glowing, new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.2F)
                            .effect(() -> glowing, 1.0F)
                            .build()))
    );

    public static final DeferredItem<Item> COOKED_SQUID_TENTACLE = ITEMS.register("cooked_squid_tentacle",
            () -> new Item(new Item.Properties()
                    .food((new FoodProperties.Builder()).nutrition(5).saturationModifier(0.6F).build()))
    );

    static MobEffectInstance luck = new MobEffectInstance(MobEffects.LUCK, 4800, 2);
    public static final DeferredItem<Item> CALAMARI_SUPREME = ITEMS.register("calamari_supreme",
            () -> new EffectTooltipItem(luck, new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    .stacksTo(1)
                    .food((new FoodProperties.Builder()).nutrition(12).saturationModifier(1.25F)
                            .effect(() -> luck, 1.0F)
                            .alwaysEdible()
                            .usingConvertsTo(Items.BOWL)
                            .build()))
    );

    public static final DeferredItem<Item> WAND = ITEMS.register("wand",
            () -> new WandItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
