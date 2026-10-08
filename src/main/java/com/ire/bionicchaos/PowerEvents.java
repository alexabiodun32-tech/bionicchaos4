package com.ire.bionicchaos;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = BionicChaos.MODID)
public class PowerEvents {
    private static final String FLY_TAG = "bionicchaos_flight";

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % 10 != 0) return;

        boolean flight = false;
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(player.getMainHandItem());
        stacks.add(player.getOffhandItem());
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() instanceof BionicSuperCoreItem) stacks.add(s);
        }
        for (ItemStack stack : stacks) {
            if (stack.getItem() instanceof PowerCoreItem core) {
                core.applyPassive(player);
                if (core.has(Power.FLIGHT)) flight = true;
            }
        }

        boolean survival = !player.isCreative() && !player.isSpectator();
        if (flight && survival) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.getPersistentData().putBoolean(FLY_TAG, true);
                player.onUpdateAbilities();
            }
        } else if (player.getPersistentData().getBoolean(FLY_TAG)) {
            player.getPersistentData().remove(FLY_TAG);
            if (survival) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }
}
