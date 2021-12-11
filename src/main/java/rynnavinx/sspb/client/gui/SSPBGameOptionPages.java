package rynnavinx.sspb.client.gui;

import com.google.common.collect.ImmutableList;

import me.jellysquid.mods.sodium.client.gui.options.*;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;

import net.minecraft.text.LiteralText;

import rynnavinx.sspb.client.gui.options.storage.SSPBOptionsStorage;

import java.util.ArrayList;
import java.util.List;


public class SSPBGameOptionPages {

    private static final SSPBOptionsStorage sspbOpts = new SSPBOptionsStorage();


    public static OptionPage sspb() {
        List<OptionGroup> groups = new ArrayList<>();
        groups.add(OptionGroup.createBuilder()
                .add(OptionImpl.createBuilder(int.class, sspbOpts)
                        .setName(new LiteralText("Shadowyness"))
                        .setTooltip(new LiteralText("Adjusts the strength of the vanilla-like inset block shadows\n\n0% - None (Sodium Default)\n85% - Default\n100% - Max"))
                        .setControl(option -> new SliderControl(option, 0, 100, 1, ControlValueFormatter.percentage()))
                        .setBinding(SSPBGameOptions::updateShadowyness, opts -> opts.shadowynessPercent)
                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                        .build()
                ).build());

        return new OptionPage(new LiteralText("SSPB"), ImmutableList.copyOf(groups));
    }
}
