package rynnavinx.sspb.common.client.options.legacy;

import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;

import rynnavinx.sspb.common.client.SSPBClientMod;
import rynnavinx.sspb.common.client.options.SSPBOptions;


public class SSPBOptionsStorage implements OptionStorage<SSPBOptions> {

    private final SSPBOptions options = SSPBClientMod.options();


    public SSPBOptions getData() {
        return this.options;
    }

    public void save() {
        this.options.save();
    }
}
