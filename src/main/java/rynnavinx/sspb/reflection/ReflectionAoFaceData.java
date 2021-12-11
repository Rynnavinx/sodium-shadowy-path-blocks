package rynnavinx.sspb.reflection;

import java.lang.reflect.Method;


public class ReflectionAoFaceData {

    public static Method getBlendedShade;
    public static Method getBlendedSkyLight;
    public static Method getBlendedBlockLight;
    public static Method hasUnpackedLightData;
    public static Method unpackLightData;

    public static void InitReflectionAoFaceData() throws Exception{
        Class<?> rAoFaceData = Class.forName("me.jellysquid.mods.sodium.client.model.light.smooth.AoFaceData");

        getBlendedShade = rAoFaceData.getMethod("getBlendedShade", float[].class);
        getBlendedSkyLight = rAoFaceData.getMethod("getBlendedSkyLight", float[].class);
        getBlendedBlockLight = rAoFaceData.getMethod("getBlendedBlockLight", float[].class);
        hasUnpackedLightData = rAoFaceData.getMethod("hasUnpackedLightData", null);
        unpackLightData = rAoFaceData.getMethod("unpackLightData", null);
    }
}
