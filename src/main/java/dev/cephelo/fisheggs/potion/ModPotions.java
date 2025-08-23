package dev.cephelo.fisheggs.potion;

import dev.cephelo.fisheggs.FishEggsMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, FishEggsMod.MODID);

    public static final Holder<Potion> GLOWING = POTIONS.register("glowing",
            () -> new Potion(new MobEffectInstance(MobEffects.GLOWING, 3600, 0)));

    public static final Holder<Potion> LONG_GLOWING = POTIONS.register("long_glowing",
            () -> new Potion(new MobEffectInstance(MobEffects.GLOWING, 9600, 0)));

    public static final Holder<Potion> BLINDNESS = POTIONS.register("blindness",
            () -> new Potion(new MobEffectInstance(MobEffects.BLINDNESS, 1800, 0)));

    public static final Holder<Potion> LONG_BLINDNESS = POTIONS.register("long_blindness",
            () -> new Potion(new MobEffectInstance(MobEffects.BLINDNESS, 4800, 0)));

    public static void register (IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
