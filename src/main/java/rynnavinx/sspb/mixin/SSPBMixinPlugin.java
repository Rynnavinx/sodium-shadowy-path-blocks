package rynnavinx.sspb.mixin;

import net.fabricmc.loader.api.FabricLoader;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import rynnavinx.sspb.client.SSPBClientMod;

import java.util.List;
import java.util.Set;


public class SSPBMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {return null;}

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(mixinClassName.equals("rynnavinx.sspb.mixin.minecraft.MixinModelLoader")) {
            if(FabricLoader.getInstance().isModLoaded("indium")) {
                SSPBClientMod.LOGGER.info("[SSPB] Indium detected. Option to toggle vanilla path block lighting will be enabled unless modded dirt path rendering is detected");
                return true;
            }
            else {
                SSPBClientMod.LOGGER.info("[SSPB] Indium not detected. Option to toggle vanilla path block lighting is disabled");
                return false;
            }
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {return null;}

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
