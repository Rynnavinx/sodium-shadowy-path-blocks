package rynnavinx.sspb.common.client.options;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.option.OptionFlag;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import rynnavinx.sspb.common.client.SSPBClientMod;


public class SSPBConfigBuilder implements ConfigEntryPoint {

    private static final SSPBOptions sspbOpts = SSPBClientMod.options();


    @Override
    public void registerConfigLate(ConfigBuilder configBuilder) {
        configBuilder.registerOwnModOptions()
                .setColorTheme(configBuilder.createColorTheme()
                        .setBaseThemeRGB(0xffcc9900)
                )
                .setNonTintedIcon(ResourceLocation.parse("sspb:textures/config-icon.png"))
                .addPage(configBuilder.createOptionPage()
                        .setName(Component.translatable("sspb.pages.sspb_page.name"))
                        .addOption(configBuilder.createIntegerOption(ResourceLocation.parse("sspb:shadowyness"))
                                .setName(Component.translatable("sspb.options.shadowyness.name"))
                                .setTooltip(Component.translatable("sspb.options.shadowyness.tooltip"))
                                .setRange(0, 100, 1)
                                .setValueFormatter(ControlValueFormatterImpls.percentage())
                                .setBinding(sspbOpts::updateShadowyness, () -> sspbOpts.shadowynessPercent)
                                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                .setStorageHandler(sspbOpts::save)
                                .setDefaultValue(SSPBOptions.DEFAULT_SHADOWYNESS_PERCENT)
                                .setEnabledProvider(state -> !(state.readBooleanOption(ResourceLocation.parse("sspb:only_affect_path_blocks")) && state.readBooleanOption(ResourceLocation.parse("sspb:vanilla_path_block_lighting"))), ResourceLocation.parse("sspb:only_affect_path_blocks"), ResourceLocation.parse("sspb:vanilla_path_block_lighting"))
                        )
                        .addOption(configBuilder.createBooleanOption(ResourceLocation.parse("sspb:only_affect_path_blocks"))//Builder(boolean.class, sspbOpts)
                                .setName(Component.translatable("sspb.options.onlyaffectpathblocks.name"))
                                .setTooltip(Component.translatable("sspb.options.onlyaffectpathblocks.tooltip"))
                                .setBinding(value -> sspbOpts.onlyAffectPathBlocks = value, () -> sspbOpts.onlyAffectPathBlocks)
                                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                .setStorageHandler(sspbOpts::save)
                                .setDefaultValue(SSPBOptions.DEFAULT_ONLY_AFFECT_PATH_BLOCKS)
                        )
                        .addOption(configBuilder.createBooleanOption(ResourceLocation.parse("sspb:vanilla_path_block_lighting"))
                                .setName(Component.translatable("sspb.options.vanillapathblocklighting.name"))
                                .setTooltip(Component.translatable("sspb.options.vanillapathblocklighting.tooltip"))
                                .setBinding(value -> sspbOpts.vanillaPathBlockLighting = value, () -> sspbOpts.vanillaPathBlockLighting)
                                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                .setStorageHandler(sspbOpts::save)
                                .setDefaultValue(SSPBOptions.DEFAULT_VANILLA_PATH_BLOCK_LIGHTING)
                        )
                );
    }
}
