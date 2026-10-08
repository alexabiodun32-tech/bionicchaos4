package com.ire.bionicchaos;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(BionicChaos.MODID)
public class BionicChaos {
    public static final String MODID = "bionicchaos";

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Bionic's Ultimate Chaos"))
                    .icon(() -> new ItemStack(ModItems.ULTIMATE_CORE.get()))
                    .displayItems((params, output) ->
                            ModItems.ITEMS.getEntries().forEach(e -> output.accept(e.get())))
                    .build());

    public BionicChaos(IEventBus modBus) {
        ModItems.ITEMS.register(modBus);
        TABS.register(modBus);
    }
}
