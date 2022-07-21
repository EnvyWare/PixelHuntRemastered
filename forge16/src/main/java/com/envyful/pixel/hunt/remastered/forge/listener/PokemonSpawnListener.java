package com.envyful.pixel.hunt.remastered.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.pixel.hunt.remastered.api.PixelHunt;
import com.envyful.pixel.hunt.remastered.api.PixelHuntFactory;
import com.envyful.pixel.hunt.remastered.forge.PixelHuntForge;
import com.envyful.pixel.hunt.remastered.forge.task.ParticleDisplayTask;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PokemonSpawnListener extends LazyListener {

    private final PixelHuntForge mod;

    public PokemonSpawnListener(PixelHuntForge mod) {
        super();

        this.mod = mod;
    }

    @SubscribeEvent
    public void onPokemonSpawn(EntityJoinWorldEvent event) {
        if (!this.mod.getConfig().isEnableParticles()) {
            return;
        }

        UtilConcurrency.runAsync(() -> {
            Entity entity = event.getEntity();

            if(!(entity instanceof PixelmonEntity)) {
                return;
            }

            PixelmonEntity pixelmon = (PixelmonEntity) entity;

            for (PixelHunt hunt : PixelHuntFactory.getAllHunts()) {
                if (hunt.isSpeciesHunted(pixelmon.getPokemon())) {
                    ParticleDisplayTask.addPokemon(pixelmon);
                    hunt.applyNickname(pixelmon);
                    return;
                }
            }
        });
    }
}
