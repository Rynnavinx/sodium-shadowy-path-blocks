package rynnavinx.sspb.mixin.sodium;

import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import rynnavinx.sspb.client.gui.SSPBGameOptionPages;

import java.util.List;


@Mixin(SodiumOptionsGUI.class)
public class MixinSodiumOptionsGUI {

    @Final @Shadow(remap = false)
    private List<OptionPage> pages;


    @Inject(method = "<init>(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void addSSPBOptionPage(CallbackInfo ci){
        this.pages.add(SSPBGameOptionPages.sspb());
    }
}
