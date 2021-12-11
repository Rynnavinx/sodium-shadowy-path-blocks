package rynnavinx.sspb;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rynnavinx.sspb.reflection.ReflectionAoFaceData;
import rynnavinx.sspb.reflection.ReflectionSmoothLightPipeline;


@Environment(EnvType.CLIENT)
public class SSPBClientMod implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("SSPB");

	@Override
	public void onInitializeClient() {
		try {
			ReflectionAoFaceData.InitReflectionAoFaceData();
			ReflectionSmoothLightPipeline.InitReflectionSmoothLightPipeline();

			LOGGER.info("[SSPB] Broken dirt path lighting is best dirt path lighting lol");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
