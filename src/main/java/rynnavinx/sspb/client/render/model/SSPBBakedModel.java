package rynnavinx.sspb.client.render.model;

import net.minecraft.client.render.model.BakedModel;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;


public class SSPBBakedModel extends ForwardingBakedModel {

    private final boolean notUsingFRAPI;


    public SSPBBakedModel(BakedModel bakedModel, boolean usingFRAPI){
        wrapped = bakedModel;
        notUsingFRAPI = !usingFRAPI;
    }

    @Override
    public boolean isVanillaAdapter() {
        return notUsingFRAPI;
    }
}
