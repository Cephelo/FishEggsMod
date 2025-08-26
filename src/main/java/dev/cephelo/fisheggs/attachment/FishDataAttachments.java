package dev.cephelo.fisheggs.attachment;

import com.mojang.serialization.Codec;
import dev.cephelo.fisheggs.FishEggsMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class FishDataAttachments {
    // Create the DeferredRegister for attachment types
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FishEggsMod.MODID);

    // Serialization via codec
    public static final Supplier<AttachmentType<Integer>> FISHINLOVE = ATTACHMENT_TYPES.register(
            "inlove", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<Integer>> BREED_COOLDOWN = ATTACHMENT_TYPES.register(
            "breed_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<Integer>> HUNT_COOLDOWN = ATTACHMENT_TYPES.register(
            "hunt_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<Boolean>> HAS_TARGET = ATTACHMENT_TYPES.register(
            "has_target", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build()
    );

    // In your mod constructor, don't forget to register the DeferredRegister to your mod bus:
    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
