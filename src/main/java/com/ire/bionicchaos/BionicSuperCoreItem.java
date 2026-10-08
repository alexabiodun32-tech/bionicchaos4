package com.ire.bionicchaos;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.EnumSet;
import java.util.List;

/** Every power in one item. Right-click: shockwave. Sneak + right-click: teleport. */
public class BionicSuperCoreItem extends PowerCoreItem {
    public BionicSuperCoreItem(Properties props) {
        super(props, allPowers());
    }

    private static EnumSet<Power> allPowers() {
        EnumSet<Power> set = EnumSet.allOf(Power.class);
        set.remove(Power.INVISIBILITY); // keeps you visible; remove this line for stealth too
        return set;
    }

    /** Works from anywhere in the inventory (see PowerEvents). Speed V, Strength X, Resistance V. */
    @Override
    public void applyPassive(Player player) {
        super.applyPassive(player);
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 4, true, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 9, true, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 4, true, false, true));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Bionic Super Core").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.literal("Grants God-like Bionic powers while held."));
        tip.add(Component.literal("Works anywhere in your inventory").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, ctx, tip, flag);
    }
}
