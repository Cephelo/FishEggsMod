package dev.cephelo.fisheggs.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class EffectTooltipItem extends Item {
    private final MobEffectInstance effect;

    public EffectTooltipItem(MobEffectInstance effect, Properties properties) {
        super(properties);
        this.effect = effect;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        PotionContents.addPotionTooltip(List.of(effect), tooltipComponents::add, 1.0F, context.tickRate());
    }
}
