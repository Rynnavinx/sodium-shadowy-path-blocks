package rynnavinx.sspb.neoforge.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;


@Mixin(ModelBlockRenderer.AmbientOcclusionRenderStorage.class)
public abstract class AmbientOcclusionRenderStorageMixin {

    // NeoForge patches Minecraft's calculate method by removing calls to move() on the result of the 5th, 6th, 7th, and 8th calls to setWithOffset()
    // This mixin just calls move() on those, effectively undoing that patch, which fixes vanilla lighting for path blocks.
    @ModifyExpressionValue(method = "calculate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos$MutableBlockPos;"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer$Cache;getShadeBrightness(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)F", ordinal = 3),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isViewBlocking(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", ordinal = 3)))
    private BlockPos.MutableBlockPos undoNeoForgeMoveRemovalPatch(BlockPos.MutableBlockPos original, BlockAndTintGetter level, BlockState state, BlockPos pos, Direction direction){
        return original.move(direction);
    }
}
