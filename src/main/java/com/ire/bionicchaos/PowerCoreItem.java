package com.ire.bionicchaos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Hold a core in either hand for its passive powers. Right-click for active powers. */
public class PowerCoreItem extends Item {
    private final Set<Power> powers;

    public PowerCoreItem(Properties props, EnumSet<Power> powers) {
        super(props);
        this.powers = powers;
    }

    public boolean has(Power p) {
        return powers.contains(p);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.literal("Hold in a hand to activate").withStyle(net.minecraft.ChatFormatting.GRAY));
        for (Power p : powers) {
            tip.add(Component.literal("• " + pretty(p)).withStyle(net.minecraft.ChatFormatting.AQUA));
        }
        if (has(Power.TELEPORT) && has(Power.SHOCKWAVE)) {
            tip.add(Component.literal("Right-click: shockwave | Sneak + right-click: teleport")
                    .withStyle(net.minecraft.ChatFormatting.YELLOW));
        }
    }

    private static String pretty(Power p) {
        String s = p.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** Called every 10 ticks (server side) for a player holding this core. */
    public void applyPassive(Player player) {
        if (has(Power.SPEED)) effect(player, MobEffects.MOVEMENT_SPEED, 3);
        if (has(Power.JUMP)) {
            effect(player, MobEffects.JUMP, 4);
            effect(player, MobEffects.SLOW_FALLING, 0);
        }
        if (has(Power.STRENGTH)) effect(player, MobEffects.DAMAGE_BOOST, 3);
        if (has(Power.REGEN)) effect(player, MobEffects.REGENERATION, 1);
        if (has(Power.INVISIBILITY)) effect(player, MobEffects.INVISIBILITY, 0);
        if (has(Power.FIRE_IMMUNITY)) effect(player, MobEffects.FIRE_RESISTANCE, 0);
        if (has(Power.WATER_BREATHING)) effect(player, MobEffects.WATER_BREATHING, 0);
        if (has(Power.FLIGHT)) effect(player, MobEffects.SLOW_FALLING, 0);
    }

    private static void effect(Player player, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> eff, int amp) {
        player.addEffect(new MobEffectInstance(eff, 40, amp, true, false, true));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        boolean used;
        if (player.isShiftKeyDown() && has(Power.TELEPORT)) {
            used = teleport(level, player);
        } else if (has(Power.SHOCKWAVE)) {
            used = shockwave((ServerLevel) level, player);
        } else if (has(Power.SCAN)) {
            used = scan(level, player);
        } else if (has(Power.TIME_SLOW)) {
            used = timeSlow(level, player);
        } else if (has(Power.TELEPORT)) {
            used = teleport(level, player);
        } else {
            used = false;
        }
        if (used) {
            player.getCooldowns().addCooldown(this, 40);
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    private boolean teleport(Level level, Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(32));
        var hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 dest = hit.getType() == HitResult.Type.MISS
                ? end
                : hit.getLocation().subtract(player.getLookAngle().scale(0.6));
        player.teleportTo(dest.x, dest.y - player.getEyeHeight() + 0.1, dest.z);
        player.fallDistance = 0;
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
        return true;
    }

    static boolean shockwave(ServerLevel level, Player player) {
        AABB area = player.getBoundingBox().inflate(8);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, area, x -> x != player)) {
            e.hurt(level.damageSources().playerAttack(player), 8f);
            Vec3 push = e.position().subtract(player.position()).normalize().scale(1.5);
            e.setDeltaMovement(push.x, 0.6, push.z);
            e.hurtMarked = true;
        }
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0, 0, 0, 0);
        level.playSound(null, player.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1f, 1f);
        return true;
    }

    private boolean timeSlow(Level level, Player player) {
        AABB area = player.getBoundingBox().inflate(16);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, area, x -> x != player)) {
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 4));
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
        }
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 2));
        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1f, 0.5f);
        return true;
    }

    private boolean scan(Level level, Player player) {
        BlockPos origin = player.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        int count = 0;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-24, -24, -24), origin.offset(24, 24, 24))) {
            BlockState s = level.getBlockState(p);
            if (s.is(Tags.Blocks.ORES)) {
                count++;
                double d = p.distSqr(origin);
                if (d < bestDist) {
                    bestDist = d;
                    best = p.immutable();
                }
            }
        }
        if (best == null) {
            player.displayClientMessage(Component.literal("Scanner: no ores within 24 blocks"), true);
        } else {
            String name = level.getBlockState(best).getBlock().getName().getString();
            player.displayClientMessage(Component.literal(String.format(
                    "Scanner: %d ores nearby. Closest: %s at %d %d %d (%.0f blocks)",
                    count, name, best.getX(), best.getY(), best.getZ(), Math.sqrt(bestDist))), false);
        }
        level.playSound(null, origin, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1f, 1.5f);
        return true;
    }
}
