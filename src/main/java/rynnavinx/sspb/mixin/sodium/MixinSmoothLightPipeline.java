package rynnavinx.sspb.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.light.data.LightDataAccess;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFlags;
import me.jellysquid.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;

import rynnavinx.sspb.reflection.ReflectionAoFaceData;
import rynnavinx.sspb.reflection.ReflectionSmoothLightPipeline;
import rynnavinx.sspb.client.SSPBClientMod;


@Mixin(SmoothLightPipeline.class)
public class MixinSmoothLightPipeline {

	private boolean offset;

	@Final @Shadow
	private LightDataAccess lightCache;

	@Shadow
	private static int getLightMapCoord(float sl, float bl) {return 0;}


	private void applyInsetPartialFace(BlockPos pos, Direction dir, float n1d, float n2d, float[] w, int i, QuadLightData out, boolean offset) throws Exception{
		Object n1 = ReflectionSmoothLightPipeline.getCachedFaceData.invoke(this, pos, dir, false);

		if(!((boolean)ReflectionAoFaceData.hasUnpackedLightData.invoke(n1))){
			ReflectionAoFaceData.unpackLightData.invoke(n1);
		}

		Object n2 = ReflectionSmoothLightPipeline.getCachedFaceData.invoke(this, pos, dir, true);

		if(!((boolean)ReflectionAoFaceData.hasUnpackedLightData.invoke(n2))){
			ReflectionAoFaceData.unpackLightData.invoke(n2);
		}

		float ao1 = (float)ReflectionAoFaceData.getBlendedShade.invoke(n1, w);
		float sl1 = (float)ReflectionAoFaceData.getBlendedSkyLight.invoke(n1, w);
		float bl1 = (float)ReflectionAoFaceData.getBlendedBlockLight.invoke(n1, w);

		float ao2 = (float)ReflectionAoFaceData.getBlendedShade.invoke(n2, w);
		float sl2 = (float)ReflectionAoFaceData.getBlendedSkyLight.invoke(n2, w);
		float bl2 = (float)ReflectionAoFaceData.getBlendedBlockLight.invoke(n2, w);

		float ao;
		float sl;
		float bl;

		BlockState blockState = lightCache.getWorld().getBlockState(pos);
		if(blockState.getBlock().isTranslucent(blockState, lightCache.getWorld(), pos)){
			// Mix between sodium inset lighting (default applyInsetPartialFace) and vanilla-like inset lighting (applyAlignedPartialFace).
			float shadowyness = SSPBClientMod.options().getShadowyness(); // vanilla-like inset lighting percentage
			float shadowynessCompliment = SSPBClientMod.options().getShadowynessCompliment(); // sodium inset lighting percentage

			if(offset){
				ao = (((ao1 * n1d) + (ao2 * n2d)) * shadowynessCompliment) + (ao2 * shadowyness);
				sl = (((sl1 * n1d) + (sl2 * n2d)) * shadowynessCompliment) + (sl2 * shadowyness);
				bl = (((bl1 * n1d) + (bl2 * n2d)) * shadowynessCompliment) + (bl2 * shadowyness);
			}
			else{
				ao = (((ao1 * n1d) + (ao2 * n2d)) * shadowynessCompliment) + (ao1 * shadowyness);
				sl = (((sl1 * n1d) + (sl2 * n2d)) * shadowynessCompliment) + (sl1 * shadowyness);
				bl = (((bl1 * n1d) + (bl2 * n2d)) * shadowynessCompliment) + (bl1 * shadowyness);
			}
		}
		else{
			// Do not apply this change to fluids or full blocks (to fix custom 3D models having dark insides)
			ao = (ao1 * n1d) + (ao2 * n2d);
			sl = (sl1 * n1d) + (sl2 * n2d);
			bl = (bl1 * n1d) + (bl2 * n2d);
		}

		out.br[i] = ao;
		out.lm[i] = getLightMapCoord(sl, bl);
	}

	@Redirect(method = "applyComplex", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/model/quad/properties/ModelQuadFlags;contains(II)Z"))
	private boolean setOffsetFieldAndReturn(int flags, int mask){
		this.offset = ModelQuadFlags.contains(flags, mask);
		return this.offset;
	}

	@Redirect(method = "applyComplex", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/model/light/smooth/SmoothLightPipeline;applyInsetPartialFace(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;FF[FILme/jellysquid/mods/sodium/client/model/light/data/QuadLightData;)V"))
	private void applyInsetPartialFaceWithOffset(SmoothLightPipeline self, BlockPos pos, Direction dir, float n1d, float n2d, float[] w, int i, QuadLightData out) throws Exception{
		applyInsetPartialFace(pos, dir, n1d, n2d, w, i, out, this.offset);
	}
}
