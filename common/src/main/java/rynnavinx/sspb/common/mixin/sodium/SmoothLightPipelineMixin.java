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

import com.mojang.blaze3d.vertex.QuadInstance;

import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.render.model.QuadViewImpl;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelLighter;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Vector3f;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import rynnavinx.sspb.common.client.SSPBClientMod;


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
	private final BlockModelLighter sspb$vanillaCalc = new BlockModelLighter();
	@Unique
	private final QuadInstance sspb$vanillaQuadInstance = new QuadInstance();
	@Unique
	private final Vector3f sspb$vanillaPos0 = new Vector3f();
	@Unique
	private final Vector3f sspb$vanillaPos1 = new Vector3f();
	@Unique
	private final Vector3f sspb$vanillaPos2 = new Vector3f();
	@Unique
	private final Vector3f sspb$vanillaPos3 = new Vector3f();

	@Unique
	private void sspb$calcVanilla(QuadViewImpl quad, float[] aoDest, int[] lightDest, BlockPos pos, Direction lightFace, boolean shade) {
		BlockAndTintGetter level = lightCache.getLevel();

		// calculateShape only uses the vertex positions and light face of the quad, so making a new BakedQuad every
		// time here is very inefficient, but this is by far the simplest choice. We don't use QuadView.toBakedQuad here
		// as it requires the sprite to be not null, it's less efficient as it needs to populate all fields correctly,
		// and it doesn't allow us to reuse Vector3f objects.
		BakedQuad bakedQuad = new BakedQuad(
				quad.copyPos(0, sspb$vanillaPos0),
				quad.copyPos(1, sspb$vanillaPos1),
				quad.copyPos(2, sspb$vanillaPos2),
				quad.copyPos(3, sspb$vanillaPos3),
				0, 0, 0, 0,
				lightFace,
				new BakedQuad.MaterialInfo(null, null, null, -1, shade, 0)
		);

		sspb$vanillaCalc.prepareQuadAmbientOcclusion(level, level.getBlockState(pos), pos, bakedQuad, sspb$vanillaQuadInstance);

		for (int i = 0; i < 4; i++) {
			// the color is expected to be fully gray, so we can pick either one and be fine.
			aoDest[i] = ARGB.redFloat(sspb$vanillaQuadInstance.getColor(i));
			lightDest[i] = sspb$vanillaQuadInstance.getLightCoords(i);
		}
	}
}
