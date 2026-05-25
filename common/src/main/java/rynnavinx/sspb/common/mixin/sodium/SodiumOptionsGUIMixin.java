package rynnavinx.sspb.common.mixin.sodium;

import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import rynnavinx.sspb.common.client.options.legacy.SSPBGameOptionPages;

import java.util.List;


// Only applied when SodiumOptionsGUI is found, meaning Sodium 0.6.x is present.
// As such, Sodium 0.6.x classes are only loaded when it is present, and the mod won't break when Sodium 0.8.12+ is present
@Pseudo @Mixin(SodiumOptionsGUI.class)
public abstract class SodiumOptionsGUIMixin {

    @Final @Shadow(remap = false)
    private List<OptionPage> pages;


    @Inject(method = "<init>*", at = @At("TAIL"))
    private void addSSPBOptionPage(CallbackInfo ci){
        this.pages.add(SSPBGameOptionPages.sspb());
    }
}
