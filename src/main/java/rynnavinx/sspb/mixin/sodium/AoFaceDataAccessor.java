package rynnavinx.sspb.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "me.jellysquid.mods.sodium.client.model.light.smooth.AoFaceData")
public interface AoFaceDataAccessor {
	@Invoker
	float invokeGetBlendedShade(float[] w);

	@Invoker
	float invokeGetBlendedSkyLight(float[] w);

	@Invoker
	float invokeGetBlendedBlockLight(float[] w);

	@Invoker
	boolean invokeHasUnpackedLightData();

	@Invoker
	void invokeUnpackLightData();
}
