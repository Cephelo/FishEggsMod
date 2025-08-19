package dev.cephelo.fisheggs.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;

public record FishEggComponents(EntityType type, int variant1, int variant2) {
    // Basic codec
    public static final Codec<FishEggComponents> FE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(FishEggComponents::type),
                    Codec.INT.fieldOf("variant1").forGetter(FishEggComponents::variant1),
                    Codec.INT.fieldOf("variant2").forGetter(FishEggComponents::variant2)
            ).apply(instance, FishEggComponents::new)
    );

    @SuppressWarnings("unchecked")
    public static final StreamCodec<RegistryFriendlyByteBuf, FishEggComponents> FE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ENTITY_TYPE), FishEggComponents::type,
            ByteBufCodecs.INT, FishEggComponents::variant1,
            ByteBufCodecs.INT, FishEggComponents::variant2,
            FishEggComponents::new
    );
}
