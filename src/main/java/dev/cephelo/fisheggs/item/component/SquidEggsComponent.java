package dev.cephelo.fisheggs.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;

public record SquidEggsComponent(EntityType type) {
    // Basic codec
    public static final Codec<SquidEggsComponent> SE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(SquidEggsComponent::type)
            ).apply(instance, SquidEggsComponent::new)
    );

    @SuppressWarnings("unchecked")
    public static final StreamCodec<RegistryFriendlyByteBuf, SquidEggsComponent> SE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ENTITY_TYPE), SquidEggsComponent::type,
            SquidEggsComponent::new
    );
}

