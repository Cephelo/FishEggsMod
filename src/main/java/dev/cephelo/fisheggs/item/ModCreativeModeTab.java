package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.FishEggsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FishEggsMod.MODID);

    public static final Supplier<CreativeModeTab> FISHEGGS_TAB = CREATIVE_MODE_TAB.register("fisheggs_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FISH_EGGS.get()))
                    .title(Component.translatable("creativetab.fisheggs.fisheggs_tab"))
                    .displayItems((itemDisplayParameters, event) -> {
                        event.accept(ModItems.FISH_FOOD);
                        event.accept(ModItems.FISH_EGGS);
                        event.accept(ModItems.SQUID_EGGS);
                        event.accept(ModItems.COOKED_SQUID_EGGS);
                        event.accept(ModItems.SQUID_TENTACLE);
                        event.accept(ModItems.GLOW_SQUID_TENTACLE);
                        event.accept(ModItems.COOKED_SQUID_TENTACLE);
                        event.accept(ModItems.CALAMARI_SUPREME);
                        event.accept(ModItems.WAND);
                    }).build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}

