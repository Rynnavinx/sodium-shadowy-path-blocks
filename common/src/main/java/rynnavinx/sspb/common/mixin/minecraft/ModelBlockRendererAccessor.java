package rynnavinx.sspb.common.mixin.minecraft;

import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;


@Mixin(ModelBlockRenderer.class)
public interface ModelBlockRendererAccessor {

    @Invoker("calculateShape")
    static void sspb$invokeCalculateShape(BlockAndTintGetter level, BlockState state, BlockPos pos, BakedQuad quad, ModelBlockRenderer.CommonRenderStorage renderStorage) { throw new AssertionError(); };
}
