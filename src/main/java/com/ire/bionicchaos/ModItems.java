package com.ire.bionicchaos;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumSet;

import static com.ire.bionicchaos.Power.*;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BionicChaos.MODID);

    public static final DeferredItem<Item> POWER_CORE = ITEMS.registerSimpleItem("power_core");

    public static final DeferredItem<PowerCoreItem> SPEED_CORE = core("speed_core", SPEED);
    public static final DeferredItem<PowerCoreItem> JUMP_CORE = core("jump_core", JUMP);
    public static final DeferredItem<PowerCoreItem> FLIGHT_CORE = core("flight_core", FLIGHT);
    public static final DeferredItem<PowerCoreItem> QUANTUM_CORE = core("quantum_core", TELEPORT, FLIGHT);
    public static final DeferredItem<PowerCoreItem> STRENGTH_CORE = core("strength_core", STRENGTH);
    public static final DeferredItem<PowerCoreItem> REGEN_CORE = core("regen_core", REGEN);
    public static final DeferredItem<PowerCoreItem> STEALTH_CORE = core("stealth_core", INVISIBILITY);
    public static final DeferredItem<PowerCoreItem> FLAME_CORE = core("flame_core", FIRE_IMMUNITY);
    public static final DeferredItem<PowerCoreItem> AQUA_CORE = core("aqua_core", WATER_BREATHING);
    public static final DeferredItem<PowerCoreItem> SCANNER_CORE = core("scanner_core", SCAN);
    public static final DeferredItem<PowerCoreItem> SHOCKWAVE_CORE = core("shockwave_core", SHOCKWAVE);
    public static final DeferredItem<PowerCoreItem> CHRONO_CORE = core("chrono_core", TIME_SLOW);
    public static final DeferredItem<PowerCoreItem> ULTIMATE_CORE = core("ultimate_core",
            SPEED, JUMP, FLIGHT, TELEPORT, STRENGTH, REGEN, FIRE_IMMUNITY, WATER_BREATHING, SHOCKWAVE);

    public static final DeferredItem<BionicSuperCoreItem> BIONIC_SUPER_CORE = ITEMS.register("bionic_super_core",
            () -> new BionicSuperCoreItem(new Item.Properties().stacksTo(1).fireResistant()));
    public static final DeferredItem<BionicSuperSword> BIONIC_SUPER_SWORD = ITEMS.register("bionic_super_sword",
            () -> new BionicSuperSword(new Item.Properties()));

    private static DeferredItem<PowerCoreItem> core(String name, Power first, Power... rest) {
        EnumSet<Power> set = EnumSet.of(first, rest);
        return ITEMS.register(name, () -> new PowerCoreItem(new Item.Properties().stacksTo(1).fireResistant(), set));
    }
}
