package dev.cephelo.fisheggs.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Objects;

public class EffectTooltipItem extends Item {

    public EffectTooltipItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        List<MobEffectInstance> list = new java.util.ArrayList<>();
        for (FoodProperties.PossibleEffect effect : Objects.requireNonNull(stack.getFoodProperties(null)).effects())
            list.add(effect.effect());

        if (!list.isEmpty())
            PotionContents.addPotionTooltip(list, tooltipComponents::add, 1.0F, context.tickRate());
    }
}
