package rynnavinx.sspb.mixin.indium;

import link.infra.indium.Indium;
import link.infra.indium.renderer.aocalc.AoCalculator;
import link.infra.indium.renderer.aocalc.AoConfig;
import link.infra.indium.renderer.render.BlockRenderInfo;

import net.minecraft.block.DirtPathBlock;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Pseudo @Mixin(AoCalculator.class)
public class MixinAoCalculator {

    @Final @Shadow
    private BlockRenderInfo blockInfo;


    @Redirect(method = "compute", at = @At(value = "FIELD", target = "Llink/infra/indium/Indium;AMBIENT_OCCLUSION_MODE:Llink/infra/indium/renderer/aocalc/AoConfig;", opcode = Opcodes.GETSTATIC), remap = false)
    private AoConfig redirectAoMode(){
        if(blockInfo.blockState.getBlock() instanceof DirtPathBlock){
            return AoConfig.EMULATE;
        }
        else{
            return Indium.AMBIENT_OCCLUSION_MODE;
        }
    }
}