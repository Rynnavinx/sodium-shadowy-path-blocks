/*
 * This file contains modified parts of AoCalculator.java from
 * "Fabric Renderer - Indigo" from "Fabric API".
 *
 * Therefore, it incorporates work under the following license:
 *
	 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *     http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
 */

package rynnavinx.sspb.common.mixin.sodium;

import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.EncodingFormat;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.QuadViewImpl;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import rynnavinx.sspb.common.client.SSPBClientMod;
import rynnavinx.sspb.common.mixin.minecraft.ModelBlockRendererAccessor;


@Mixin(value = SmoothLightPipeline.class)
public abstract class SmoothLightPipelineMixin {

	@Final @Shadow(remap = false)
	private LightDataAccess lightCache;


	@Unique
	private float sspb$getModifiedAOWeight(float originalWeight, BlockPos pos){
		BlockState blockState = lightCache.getLevel().getBlockState(pos);
		boolean onlyAffectPathBlocks = SSPBClientMod.options().onlyAffectPathBlocks;

		if((!onlyAffectPathBlocks && blockState.propagatesSkylightDown()) ||
				(onlyAffectPathBlocks && blockState.getBlock() instanceof DirtPathBlock)){

			// Mix between actual and full shadowyness, to mix between fixed sodium lighting and bugged vanilla lighting, respectively
			return (originalWeight * SSPBClientMod.options().getShadowynessCompliment()) + SSPBClientMod.options().getShadowyness();
		}

		return originalWeight;
	}


	// Ideally the following two mixins would be done by modifying depth directly on applyParallelFace and applyNonParallelFace,
	// but the package private AoNeighborInfo in the method signature prevents me from doing that
	@ModifyVariable(method = "applyInsetPartialFaceVertex", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float modifyApplyInsetPartialFaceVertexN1d(float n1d, BlockPos pos){
		return sspb$getModifiedAOWeight(n1d, pos);
	}

	@ModifyVariable(method = "applyInsetPartialFaceVertex", at = @At(value = "HEAD", shift = At.Shift.BY, by = 1), argsOnly = true, ordinal = 1)
	private float modifyApplyInsetPartialFaceVertexN2d(float n2d, BlockPos pos, Direction dir, float n1d){
		return 1.0f - n1d;
	}

	@ModifyVariable(method = "gatherInsetFace", at = @At("STORE"), ordinal = 0)
	private float modifyGatherInsetFaceW1(float w1, ModelQuadView quad, BlockPos blockPos){
		return sspb$getModifiedAOWeight(w1, blockPos);
	}

	@Inject(method = "calculate", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/light/smooth/SmoothLightPipeline;applyParallelFace(Lnet/caffeinemc/mods/sodium/client/model/light/smooth/AoNeighborInfo;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/caffeinemc/mods/sodium/client/model/light/data/QuadLightData;Z)V", shift = At.Shift.BEFORE), cancellable = true)
	private void injectVanillaAoCalcForPathBlocks(ModelQuadView quad, BlockPos pos, QuadLightData out, Direction cullFace, Direction lightFace, boolean shade, boolean isFluid, CallbackInfo ci){
		if(SSPBClientMod.options().vanillaPathBlockLighting && lightCache.getLevel().getBlockState(pos).getBlock() instanceof DirtPathBlock){
			sspb$calcVanilla((QuadViewImpl) quad, out.br, out.lm, pos, lightFace, shade);
			ci.cancel();
		}
	}


	/*
	 * From here to the end of the file is code adapted from AoCalculator.java from
	 * "Fabric Renderer - Indigo" from "Fabric API".
	 */

	// These are what vanilla AO calc wants, per its usage in vanilla code
	// Because this instance is effectively thread-local, we preserve instances
	// to avoid making a new allocation each call.
	@Unique
	private final int[] sspb$vertexData = new int[EncodingFormat.QUAD_STRIDE];
	@Unique
	private final ModelBlockRenderer.AmbientOcclusionRenderStorage sspb$vanillaCalc = new ModelBlockRenderer.AmbientOcclusionRenderStorage();


	@Unique
	private void sspb$calcVanilla(QuadViewImpl quad, float[] aoDest, int[] lightDest, BlockPos pos, Direction lightFace, boolean shade) {
		quad.toVanilla(sspb$vertexData, 0);

		BlockAndTintGetter level = lightCache.getLevel();

		ModelBlockRendererAccessor.sspb$invokeCalculateShape(level, level.getBlockState(pos), pos, sspb$vertexData, lightFace, sspb$vanillaCalc);
		sspb$vanillaCalc.calculate(level, level.getBlockState(pos), pos, lightFace, shade);

		System.arraycopy(sspb$vanillaCalc.brightness, 0, aoDest, 0, 4);
		System.arraycopy(sspb$vanillaCalc.lightmap, 0, lightDest, 0, 4);
	}
}
