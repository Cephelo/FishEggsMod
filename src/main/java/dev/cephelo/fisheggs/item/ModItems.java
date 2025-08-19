package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.item.component.FishEggComponents;
import dev.cephelo.fisheggs.item.component.ModDataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FishEggsMod.MODID);

    public static final DeferredItem<Item> FISH_EGGS = ITEMS.register("fish_eggs",
            () -> new FishEggsItem(new Item.Properties()
                    .component(ModDataComponents.FE_COMP.value(), new FishEggComponents(EntityType.COD, 0, 0)))
            );

    public static final DeferredItem<Item> FISH_FOOD = ITEMS.register("fish_food",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
