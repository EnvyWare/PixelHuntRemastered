package com.envyful.pixel.hunt.remastered.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.data.Serializers;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.reforged.pixelmon.config.PokemonGeneratorConfig;
import com.envyful.pixel.hunt.remastered.forge.config.typeadapter.ParticleDataTypeAdapter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigPath("config/PixelHuntRemastered/config.yml")
@ConfigSerializable
@Serializers(ParticleDataTypeAdapter.class)
public class PixelHuntConfig extends AbstractYamlConfig {

    private ConfigInterface configInterface = new ConfigInterface();

    private boolean enableParticles = true;

    private Map<String, HuntConfig> hunts = ImmutableMap.of(
            "one", new HuntConfig()
    );

    public PixelHuntConfig() {
        super();
    }

    public ConfigInterface getConfigInterface() {
        return this.configInterface;
    }

    public boolean isEnableParticles() {
        return this.enableParticles;
    }

    public Map<String, HuntConfig> getHunts() {
        return this.hunts;
    }

    @ConfigSerializable
    public static class HuntConfig {

        private String displayName = "&b%species%";
        private List<String> extraLore = Lists.newArrayList(
                "",
                "&bTime remaining: %time%"
        );

        private List<String> preLore = Lists.newArrayList(
                "",
                "This goes at the top"
        );
        private String pokemonNickname = "§6> §f%species% §6<";
        private String descriptionColour = "§a";
        private String descriptionOffColour = "§b";
        private IParticleData particles = ParticleTypes.FLAME;
        private List<String> spawnBroadcast = Lists.newArrayList();
        private List<String> timeoutBroadcast = Lists.newArrayList();
        private List<String> rewardBroadcast = Lists.newArrayList("broadcast Rewarded %player%");
        private List<String> rewardCommands = Lists.newArrayList("broadcast Testing %player%");
        private List<String> rewardDescription = Lists.newArrayList("Hello");
        private PokemonGeneratorConfig generatorConfig = new PokemonGeneratorConfig(
                Sets.newHashSet(), true, false, false, true,
                false, false, 2, 2,
                false, true, false, 30, 100,
                false
        );
        private boolean randomCommands = false;
        private boolean maxIvs = false;
        private boolean ivMultiplierEnabled = false;
        private float ivMultiplier = 1.5f;
        private long maxDurationMinutes = 30;
        private int guiX = 1;
        private int guiY = 1;

        public HuntConfig() {}

        public String getDisplayName() {
            return this.displayName;
        }

        public List<String> getExtraLore() {
            return this.extraLore;
        }

        public List<String> getPreLore() {
            return this.preLore;
        }

        public String getPokemonNickname() {
            return this.pokemonNickname;
        }

        public String getDescriptionColour() {
            return this.descriptionColour;
        }

        public String getDescriptionOffColour() {
            return this.descriptionOffColour;
        }

        public IParticleData getParticles() {
            return this.particles;
        }

        public List<String> getSpawnBroadcast() {
            return this.spawnBroadcast;
        }

        public List<String> getTimeoutBroadcast() {
            return this.timeoutBroadcast;
        }

        public List<String> getRewardBroadcast() {
            return this.rewardBroadcast;
        }

        public List<String> getRewardCommands() {
            return this.rewardCommands;
        }

        public List<String> getRewardDescription() {
            return this.rewardDescription;
        }

        public PokemonGeneratorConfig getGeneratorConfig() {
            return this.generatorConfig;
        }

        public boolean isRandomCommands() {
            return this.randomCommands;
        }

        public boolean isMaxIvs() {
            return this.maxIvs;
        }

        public boolean isIvMultiplierEnabled() {
            return this.ivMultiplierEnabled;
        }

        public float getIvMultiplier() {
            return this.ivMultiplier;
        }

        public long getMaxDurationMinutes() {
            return this.maxDurationMinutes;
        }

        public int getGuiX() {
            return this.guiX;
        }

        public int getGuiY() {
            return this.guiY;
        }
    }
}
