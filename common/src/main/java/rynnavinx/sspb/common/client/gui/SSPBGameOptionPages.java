//package rynnavinx.sspb.common.client.gui;
//
//import com.google.common.collect.ImmutableList;
//
//import net.caffeinemc.mods.sodium.client.gui.options.OptionFlag;
//import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
//import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
//import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
//import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
//import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
//import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
//
//import net.minecraft.network.chat.Component;
//
//import rynnavinx.sspb.common.client.gui.options.storage.SSPBOptionsStorage;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class SSPBGameOptionPages {
//
//    private static final SSPBOptionsStorage sspbOpts = new SSPBOptionsStorage();
//
//
//    public static OptionPage sspb() {
//        List<OptionGroup> groups = new ArrayList<>();
//        groups.add(OptionGroup.createBuilder()
//                .add(OptionImpl.createBuilder(int.class, sspbOpts)
//                        .setName(Component.translatable("sspb.options.shadowyness.name"))
//                        .setTooltip(Component.translatable("sspb.options.shadowyness.tooltip"))
//                        .setControl(option -> new SliderControl(option, 0, 100, 1, ControlValueFormatter.percentage()))
//                        .setBinding(SSPBGameOptions::updateShadowyness, opts -> opts.shadowynessPercent)
//                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
//                        .build())
//                .add(OptionImpl.createBuilder(boolean.class, sspbOpts)
//                        .setName(Component.translatable("sspb.options.onlyaffectpathblocks.name"))
//                        .setTooltip(Component.translatable("sspb.options.onlyaffectpathblocks.tooltip"))
//                        .setControl(TickBoxControl::new)
//                        .setBinding((opts, value) -> opts.onlyAffectPathBlocks = value, opts -> opts.onlyAffectPathBlocks)
//                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
//                        .build())
//                .add(OptionImpl.createBuilder(boolean.class, sspbOpts)
//                        .setName(Component.translatable("sspb.options.vanillapathblocklighting.name"))
//                        .setTooltip(Component.translatable("sspb.options.vanillapathblocklighting.tooltip"))
//                        .setControl(TickBoxControl::new)
//                        .setBinding((opts, value) -> opts.vanillaPathBlockLighting = value, opts -> opts.vanillaPathBlockLighting)
//                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
//                        .build())
//                .build());
//
//        return new OptionPage(Component.translatable("sspb.pages.sspb_page.name"), ImmutableList.copyOf(groups));
//    }
//}
