package rynnavinx.sspb.common.client.options;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rynnavinx.sspb.common.client.SSPBClientMod;
import rynnavinx.sspb.common.services.IPlatformHelper;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;


public class SSPBOptions {

    private static final String DEFAULT_FILE_NAME = "sodium-shadowy-path-blocks-options.json";
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .create();
    private Path configPath;

    public static final int DEFAULT_SHADOWYNESS_PERCENT = 85;
    public static final boolean DEFAULT_ONLY_AFFECT_PATH_BLOCKS = true;
    public static final boolean DEFAULT_VANILLA_PATH_BLOCK_LIGHTING = false;

    public int shadowynessPercent; // only used so the slider in the options can display the value as a proper percentage
    private float shadowyness;
    private float shadowynessCompliment;

    public boolean onlyAffectPathBlocks;

    public boolean vanillaPathBlockLighting;


    public SSPBOptions(){
        shadowynessPercent = DEFAULT_SHADOWYNESS_PERCENT;
        shadowyness = 0.85f;
        shadowynessCompliment = 0.15f;

        onlyAffectPathBlocks = DEFAULT_ONLY_AFFECT_PATH_BLOCKS;

        vanillaPathBlockLighting = DEFAULT_VANILLA_PATH_BLOCK_LIGHTING;
    }


    public void updateShadowyness(int shadowynessPercent){
        this.shadowynessPercent = shadowynessPercent;

        shadowyness = shadowynessPercent/100f;
        shadowynessCompliment = 1 - (shadowynessPercent/100f);
    }

    public float getShadowyness(){
        return shadowyness;
    }

    public float getShadowynessCompliment(){
        return shadowynessCompliment;
    }


    public void save() {
        try {
            writeChanges();
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't save SSPB options changes", e);
        }

        SSPBClientMod.LOGGER.info("[SSPB] Saved changes to SSPB options");
    }

    public static SSPBOptions load() {
        Path path = IPlatformHelper.INSTANCE.getConfigDirectory().resolve(DEFAULT_FILE_NAME);
        SSPBOptions config;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, SSPBOptions.class);
            }
            catch (IOException e) {
                throw new RuntimeException("Could not parse SSPB options", e);
            }
        }
        else {
            config = new SSPBOptions();
        }

        config.configPath = path;

        try {
            config.writeChanges();
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't update SSPB options", e);
        }

        config.updateShadowyness(config.shadowynessPercent);

        return config;
    }

    private void writeChanges() throws IOException {
        Path dir = this.configPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        Files.writeString(this.configPath, GSON.toJson(this));
    }

}
