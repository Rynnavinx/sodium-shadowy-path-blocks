package rynnavinx.sspb.common.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rynnavinx.sspb.common.client.options.SSPBOptions;


public class SSPBClientMod {

	public static final Logger LOGGER = LoggerFactory.getLogger("SSPB");

	private static final SSPBOptions CONFIG = SSPBOptions.load();


	public static SSPBOptions options() {
		return CONFIG;
	}

	public static void onInitClient() {
		LOGGER.info("[SSPB] Broken dirt path lighting is best dirt path lighting lol");
	}
}
