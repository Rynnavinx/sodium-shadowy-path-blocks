package rynnavinx.sspb.reflection;

import java.lang.reflect.Method;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class ReflectionSmoothLightPipeline {

    public static Method getCachedFaceData;

    public static void InitReflectionSmoothLightPipeline() throws Exception{
        Class<?> rSmoothLightPipeline = Class.forName("me.jellysquid.mods.sodium.client.model.light.smooth.SmoothLightPipeline");

        getCachedFaceData = rSmoothLightPipeline.getDeclaredMethod("getCachedFaceData", BlockPos.class, Direction.class, boolean.class);
    }
}
