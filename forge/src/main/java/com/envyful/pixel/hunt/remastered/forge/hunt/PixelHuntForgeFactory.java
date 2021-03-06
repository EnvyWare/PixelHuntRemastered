package com.envyful.pixel.hunt.remastered.forge.hunt;

import com.envyful.pixel.hunt.remastered.api.PixelHunt;
import com.envyful.pixel.hunt.remastered.api.factory.PixelHuntPlatformFactory;
import com.envyful.pixel.hunt.remastered.forge.PixelHuntForge;
import com.envyful.pixel.hunt.remastered.forge.config.PixelHuntConfig;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public class PixelHuntForgeFactory implements PixelHuntPlatformFactory {

    private final List<PixelHunt> loadedHunts = Lists.newArrayList();

    private final PixelHuntForge mod;

    public PixelHuntForgeFactory(PixelHuntForge mod) {
        this.mod = mod;
        this.reloadHunts();
    }

    @Override
    public void reloadHunts() {
        this.loadedHunts.clear();

        for (PixelHuntConfig.HuntConfig value : PixelHuntForge.getInstance().getConfig().getHunts().values()) {
            this.loadedHunts.add(new ForgePixelHunt(value));
        }
    }

    @Override
    public List<PixelHunt> getAllHunts() {
        return this.loadedHunts;
    }
}
