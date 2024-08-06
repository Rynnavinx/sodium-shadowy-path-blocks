package rynnavinx.sspb.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.light.data.LightDataAccess;
import me.jellysquid.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirtPathBlock;

import rynnavinx.sspb.client.SSPBClientMod;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;


@Mixin(SmoothLightPipeline.class)
public abstract class MixinSmoothLightPipeline {

	@Final @Shadow(remap = false)
	private LightDataAccess lightCache;

	@Shadow(remap = false)
	private static int getLightMapCoord(float sl, float bl) {return 0;}

	@Unique
	private static final MethodHandle sspb$getCachedFaceDataHandle;

	static {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			Class<?> aoFaceDataClass = Class.forName("me.jellysquid.mods.sodium.client.model.light.smooth.AoFaceData");

			sspb$getCachedFaceDataHandle = lookup.findVirtual(SmoothLightPipeline.class, "getCachedFaceData", MethodType.methodType(aoFaceDataClass, BlockPos.class, Direction.class, boolean.class));
		} catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Unique
	@SuppressWarnings({"JavaLangInvokeHandleSignature", "DataFlowIssue"})
	private AoFaceDataAccessor sspb$getCachedFaceData(BlockPos pos, Direction dir, boolean offset){
		try {
			return (AoFaceDataAccessor) sspb$getCachedFaceDataHandle.invoke((SmoothLightPipeline) (Object) this, pos, dir, offset);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}


	@Unique
	private void sspb$applyInsetPartialFaceVertex(BlockPos pos, Direction dir, float n1d, float n2d, float[] w, int i, QuadLightData out, boolean isParallel){
		AoFaceDataAccessor n1 = sspb$getCachedFaceData(pos, dir, false);

		if(!n1.invokeHasUnpackedLightData()){
			n1.invokeUnpackLightData();
		}

		AoFaceDataAccessor n2 = sspb$getCachedFaceData(pos, dir, true);

		if(!n2.invokeHasUnpackedLightData()){
			n2.invokeUnpackLightData();
		}

		float ao1 = n1.invokeGetBlendedShade(w);
		float sl1 = n1.invokeGetBlendedSkyLight(w);
		float bl1 = n1.invokeGetBlendedBlockLight(w);

		float ao2 = n2.invokeGetBlendedShade(w);
		float sl2 = n2.invokeGetBlendedSkyLight(w);
		float bl2 = n2.invokeGetBlendedBlockLight(w);

		float ao;
		float sl;
		float bl;

		BlockState blockState = lightCache.getWorld().getBlockState(pos);
		boolean onlyAffectPathBlocks = SSPBClientMod.options().onlyAffectPathBlocks;

		if((!onlyAffectPathBlocks && blockState.isTransparent(lightCache.getWorld(), pos)) ||
				(isParallel && onlyAffectPathBlocks && blockState.getBlock() instanceof DirtPathBlock)){

			// Mix between sodium inset lighting (default applyInsetPartialFaceVertex) and vanilla-like inset lighting (applyAlignedPartialFaceVertex).
			float shadowyness = SSPBClientMod.options().getShadowyness(); // vanilla-like inset lighting percentage
			float shadowynessCompliment = SSPBClientMod.options().getShadowynessCompliment(); // sodium inset lighting percentage

			ao = (((ao1 * n1d) + (ao2 * n2d)) * shadowynessCompliment) + (ao1 * shadowyness);
			sl = (((sl1 * n1d) + (sl2 * n2d)) * shadowynessCompliment) + (sl1 * shadowyness);
			bl = (((bl1 * n1d) + (bl2 * n2d)) * shadowynessCompliment) + (bl1 * shadowyness);
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

	@Redirect(method = "applyParallelFace", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/model/light/smooth/SmoothLightPipeline;applyInsetPartialFaceVertex(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;FF[FILme/jellysquid/mods/sodium/client/model/light/data/QuadLightData;)V"))
	private void redirectParallelApplyInset(SmoothLightPipeline self, BlockPos pos, Direction dir, float n1d, float n2d, float[] w, int i, QuadLightData out){
		sspb$applyInsetPartialFaceVertex(pos, dir, n1d, n2d, w, i, out, true);
	}

	@Redirect(method = "applyNonParallelFace", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/model/light/smooth/SmoothLightPipeline;applyInsetPartialFaceVertex(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;FF[FILme/jellysquid/mods/sodium/client/model/light/data/QuadLightData;)V"))
	private void redirectNonParallelApplyInset(SmoothLightPipeline self, BlockPos pos, Direction dir, float n1d, float n2d, float[] w, int i, QuadLightData out){
		sspb$applyInsetPartialFaceVertex(pos, dir, n1d, n2d, w, i, out, false);
	}
}
