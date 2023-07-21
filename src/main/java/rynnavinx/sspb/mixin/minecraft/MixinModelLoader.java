package rynnavinx.sspb.mixin.minecraft;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    private void wrapDirtPathModel(Identifier id){
        if(id.getNamespace().equals("minecraft") && id.getPath().equals("dirt_path") && ((ModelIdentifier)id).getVariant().equals("")) {
            BakedModel originalBakedModel = bakedModels.get(id);
            // wrap if not using frapi
            if(!(originalBakedModel instanceof FabricBakedModel) || ((FabricBakedModel) originalBakedModel).isVanillaAdapter()){
                bakedModels.replace(id, new SSPBBakedModel(bakedModels.get(id), SSPBClientMod.options().vanillaPathBlockLighting));
                SSPBGameOptionPages.setVanillaPathBlockLightingOptEnabled(true);
                LOGGER.info("[SSPB] Option to toggle vanilla path block lighting is enabled");
            }
            else{
                SSPBGameOptionPages.setVanillaPathBlockLightingOptEnabled(false);
                LOGGER.info("[SSPB] Modded dirt path rendering detected. Option to toggle vanilla path block lighting is disabled.");
            }
        }
    }


   /* These two mixins target the same lambda, but the lambda changed position in the code from 1.19 to 1.20,
    * which changed the intermediate mapping name, so I'm injecting like this for compatibility reasons.
    */

    //Targets lambda "method_4733" at the bottom of method "upload" in 1.19
    @Dynamic()
    @Inject(method = "method_4733", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER), require = 0)
    private void wrapDirtPathModel1_19(Identifier id, CallbackInfo ci) {
        wrapDirtPathModel(id);
    }

    //Targets lambda "method_45877" in the method "bake" in 1.20
    @Dynamic()
    @Inject(method = "method_45877", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER), require = 0)
    private void wrapDirtPathModel1_20(BiFunction biFunction, Identifier modelId, CallbackInfo ci) {
        wrapDirtPathModel(modelId);
    }
}
