package com.envyful.pixel.hunt.remastered.forge.task;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.pixel.hunt.remastered.api.PixelHunt;
import com.envyful.pixel.hunt.remastered.api.PixelHuntFactory;
import com.envyful.pixel.hunt.remastered.forge.PixelHuntForge;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.List;

public class ParticleDisplayTask extends LazyListener {

    private static ParticleDisplayTask instance;

    private final List<PixelmonEntity> huntPokemon = Lists.newArrayList();

    private int currentTick = 0;

    public ParticleDisplayTask() {
        super();

        instance = this;
    }

    public static void addPokemon(PixelmonEntity pixelmon) {
        instance.huntPokemon.add(pixelmon);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (!PixelHuntForge.getInstance().getConfig().isEnableParticles()) {
            return;
        }

        ++this.currentTick;

        if (this.currentTick % 10 != 0) {
            return;
        }

        Iterator<PixelmonEntity> iterator = this.huntPokemon.iterator();
        UtilConcurrency.runAsync(() -> {
            while (iterator.hasNext()) {
                PixelmonEntity pixelmon = iterator.next();

                if (pixelmon == null || !pixelmon.isAlive() || pixelmon.hasOwner()) {
                    iterator.remove();
                    continue;
                }

                for (PixelHunt hunt : PixelHuntFactory.getAllHunts()) {
                    if (hunt.isSpeciesHunted(pixelmon.getPokemon())) {
                        hunt.spawnParticle(pixelmon);
                    }
                }
            }

            for (PixelHunt allHunt : PixelHuntFactory.getAllHunts()) {
                if (allHunt.hasTimedOut()) {
                    allHunt.end();
                    allHunt.generatePokemon();
                }
            }
        });
    }
}
