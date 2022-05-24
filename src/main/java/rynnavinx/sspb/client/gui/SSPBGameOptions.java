package rynnavinx.sspb.client.gui;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;


public class SSPBGameOptions {

    private static final String DEFAULT_FILE_NAME = "sodium-shadowy-path-blocks-options.json";
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();
    private Path configPath;

    public int shadowynessPercent; // only used so the slider in the options can display the value as a proper percentage
    private float shadowyness;
    private float shadowynessCompliment;

    public boolean onlyAffectPathBlocks;


    public SSPBGameOptions(){
        shadowynessPercent = 85;
        shadowyness = 0.85f;
        shadowynessCompliment = 0.15f;

        onlyAffectPathBlocks = false;
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

    public static SSPBGameOptions load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(DEFAULT_FILE_NAME);
        SSPBGameOptions config;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, SSPBGameOptions.class);
            }
            catch (IOException e) {
                throw new RuntimeException("Could not parse SSPB config", e);
            }
        }
        else {
            config = new SSPBGameOptions();
        }

        config.configPath = path;

        try {
            config.writeChanges();
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't update SSPB config", e);
        }

        return config;
    }

    public void writeChanges() throws IOException {
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
