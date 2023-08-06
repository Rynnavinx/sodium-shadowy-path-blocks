package rynnavinx.sspb.mixin.minecraft;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rynnavinx.sspb.client.SSPBClientMod;
import rynnavinx.sspb.client.gui.SSPBGameOptionPages;
import rynnavinx.sspb.client.render.model.SSPBBakedModel;

import java.util.Map;
import java.util.function.BiFunction;

import static rynnavinx.sspb.client.SSPBClientMod.LOGGER;


@Mixin(ModelLoader.class)
public class MixinModelLoader {

    @Final @Shadow
    private Map<Identifier, BakedModel> bakedModels;


    @Unique
    private void wrapDirtPathModel(){
        ModelIdentifier id = new ModelIdentifier("minecraft", "dirt_path", "");
        BakedModel originalBakedModel = bakedModels.get(id);
        if(originalBakedModel != null){
            // wrap if not using frapi
            if(!(originalBakedModel instanceof FabricBakedModel) || ((FabricBakedModel) originalBakedModel).isVanillaAdapter()){
                bakedModels.replace(id, new SSPBBakedModel(originalBakedModel, SSPBClientMod.options().vanillaPathBlockLighting));
                SSPBGameOptionPages.setVanillaPathBlockLightingOptEnabled(true);
                LOGGER.info("[SSPB] Option to toggle vanilla path block lighting is enabled");
            }
            else{
                SSPBGameOptionPages.setVanillaPathBlockLightingOptEnabled(false);
                LOGGER.info("[SSPB] Modded dirt path rendering detected. Option to toggle vanilla path block lighting is disabled.");
            }
        }
        else{
            LOGGER.warn("[SSPB] Dirt Path model was not found");
        }
    }

    /* These two mixins target the same bit of code that got refactored in 1.19.3,
     * so I'm injecting like this for compatibility reasons.
     */

    @Inject(method = "upload", at = @At(value = "INVOKE", target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER), require = 0)
    private void wrapDirtPathModelPre1_19_3(TextureManager textureManager, Profiler profiler, CallbackInfoReturnable<SpriteAtlasManager> cir){
        wrapDirtPathModel();
    }

    // Using intermediary name because the yarn mapped name "bake" is a separate method pre-1.19.3, which led to issues injecting
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"}) // Suppress because the method is not in this project's MC version. Compiler still complains though...
    @Inject(method = "method_45876(Ljava/util/function/BiFunction;)V", at = @At(value = "INVOKE", target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER), require = 0)
    private void wrapDirtPathModel1_19_3(BiFunction<Identifier, SpriteIdentifier, Sprite> spriteLoader, CallbackInfo ci){
        wrapDirtPathModel();
    }
}
