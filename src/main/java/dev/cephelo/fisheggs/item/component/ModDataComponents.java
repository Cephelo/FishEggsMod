package dev.cephelo.fisheggs.item.component;

import dev.cephelo.fisheggs.FishEggsMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FishEggsMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FishEggComponents>> FE_COMP =
            REGISTRAR.registerComponentType(
            "fe_comp",
            builder -> builder
                    .persistent(FishEggComponents.FE_CODEC)
                    .networkSynchronized(FishEggComponents.FE_STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SquidEggsComponent>> SE_COMP =
            REGISTRAR.registerComponentType(
                    "se_comp",
                    builder -> builder
                            .persistent(SquidEggsComponent.SE_CODEC)
                            .networkSynchronized(SquidEggsComponent.SE_STREAM_CODEC)
            );

    public static void register(IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }

}
