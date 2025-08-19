package dev.cephelo.fisheggs.sound;

import dev.cephelo.fisheggs.FishEggsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, FishEggsMod.MODID);

    public static final Supplier<SoundEvent> FISH_EATS = registerSoundEvent("fish_eats");
    public static final Supplier<SoundEvent> FISH_BREEDS = registerSoundEvent("fish_breeds");
    public static final Supplier<SoundEvent> EGGS_HATCH = registerSoundEvent("eggs_hatch");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FishEggsMod.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}

