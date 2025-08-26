package dev.cephelo.fisheggs.mixin;

import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFish.class)
public class FishBucketDataMixin {

    @Inject(method = "saveToBucketTag", at = @At("HEAD"))
    private void saveAttachments(ItemStack stack, CallbackInfo ci) {
        if ((Object)this instanceof AbstractFish fish) {
            CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, (compoundTag) ->
                    compoundTag.putInt("FishEggsInLove", (fish.getData(FishDataAttachments.FISHINLOVE))));
            CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, (compoundTag) ->
                    compoundTag.putInt("FishEggsBreedCooldown", (fish.getData(FishDataAttachments.BREED_COOLDOWN))));
        }
    }

    @Inject(method = "loadFromBucketTag", at = @At("HEAD"))
    private void loadAttachments(CompoundTag tag, CallbackInfo ci) {
        if ((Object)this instanceof AbstractFish fish) {
            if (tag.contains("FishEggsInLove"))
                fish.setData(FishDataAttachments.FISHINLOVE, tag.getInt("FishEggsInLove"));
            if (tag.contains("FishEggsBreedCooldown"))
                fish.setData(FishDataAttachments.BREED_COOLDOWN, tag.getInt("FishEggsBreedCooldown"));
        }
    }
}