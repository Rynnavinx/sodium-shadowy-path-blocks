//package rynnavinx.sspb.common.client.gui.options.storage;
//
//import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
//
//import rynnavinx.sspb.common.client.SSPBClientMod;
//import rynnavinx.sspb.common.client.gui.SSPBGameOptions;
//
//import java.io.IOException;
//
//
//public class SSPBOptionsStorage implements OptionStorage<SSPBGameOptions> {
//
//    private final SSPBGameOptions options = SSPBClientMod.options();
//
//
//    public SSPBGameOptions getData() {
//        return this.options;
//    }
//
//    public void save() {
//        try {
//            this.options.writeChanges();
//        }
//        catch (IOException e) {
//            throw new RuntimeException("Couldn't save SSPB config changes", e);
//        }
//
//        SSPBClientMod.LOGGER.info("[SSPB] Saved changes to SSPB config");
//    }
//}
