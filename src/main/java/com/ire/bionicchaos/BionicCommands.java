package com.ire.bionicchaos;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * /bionic give <super_core|super_sword|all>
 * /bionic god | fly | heal | lightning | shockwave
 * Requires operator (permission level 2) / cheats enabled.
 */
@EventBusSubscriber(modid = BionicChaos.MODID)
public class BionicCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bionic")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("give")
                        .then(Commands.literal("super_core").executes(c -> give(c.getSource(), ModItems.BIONIC_SUPER_CORE.get())))
                        .then(Commands.literal("super_sword").executes(c -> give(c.getSource(), ModItems.BIONIC_SUPER_SWORD.get())))
                        .then(Commands.literal("all").executes(c -> give(c.getSource(),
                                ModItems.BIONIC_SUPER_CORE.get(), ModItems.BIONIC_SUPER_SWORD.get()))))
                .then(Commands.literal("god").executes(c -> god(c.getSource())))
                .then(Commands.literal("fly").executes(c -> fly(c.getSource())))
                .then(Commands.literal("heal").executes(c -> heal(c.getSource())))
                .then(Commands.literal("lightning").executes(c -> lightning(c.getSource())))
                .then(Commands.literal("shockwave").executes(c -> shockwave(c.getSource()))));
    }

    private static int give(CommandSourceStack src, Item... items) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        for (Item item : items) {
            ItemStack stack = new ItemStack(item);
            if (!p.getInventory().add(stack)) {
                p.drop(stack, false);
            }
        }
        say(src, "Bionic items given.");
        return 1;
    }

    private static int god(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        boolean on = !p.getAbilities().invulnerable;
        p.getAbilities().invulnerable = on;
        p.onUpdateAbilities();
        say(src, "God mode " + (on ? "ON" : "OFF"));
        return 1;
    }

    private static int fly(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        boolean on = !p.getAbilities().mayfly;
        p.getAbilities().mayfly = on;
        if (!on) p.getAbilities().flying = false;
        p.onUpdateAbilities();
        say(src, "Flight " + (on ? "ON" : "OFF"));
        return 1;
    }

    private static int heal(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        p.setHealth(p.getMaxHealth());
        p.getFoodData().setFoodLevel(20);
        p.getFoodData().setSaturation(20f);
        say(src, "Fully healed.");
        return 1;
    }

    private static int lightning(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        ServerLevel level = p.serverLevel();
        Vec3 eye = p.getEyePosition();
        Vec3 end = eye.add(p.getLookAngle().scale(100));
        var hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
        Vec3 target = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(target.x, target.y, target.z);
            bolt.setCause(p);
            level.addFreshEntity(bolt);
        }
        return 1;
    }

    private static int shockwave(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer p = src.getPlayerOrException();
        PowerCoreItem.shockwave(p.serverLevel(), p);
        return 1;
    }

    private static void say(CommandSourceStack src, String msg) {
        src.sendSuccess(() -> Component.literal(msg), false);
    }
}
