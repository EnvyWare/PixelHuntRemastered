package com.envyful.pixel.hunt.remastered.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.pixel.hunt.remastered.api.PixelHunt;
import com.envyful.pixel.hunt.remastered.api.PixelHuntFactory;
import com.envyful.pixel.hunt.remastered.forge.PixelHuntForge;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PokemonCaptureListener {

    private final PixelHuntForge mod;

    public PokemonCaptureListener(PixelHuntForge mod) {
        this.mod = mod;

        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPokemonCaught(CaptureEvent.SuccessfulCapture event) {
        event.getPokemon().getPokemon().removeNickname();
        UtilConcurrency.runAsync(() -> {
            Pokemon caught = event.getPokemon().getPokemon();
            ServerPlayerEntity player = event.player;
            ForgeEnvyPlayer envyPlayer = this.mod.getPlayerManager().getPlayer(player);

            for (PixelHunt hunt : PixelHuntFactory.getAllHunts()) {
                if (hunt.isBeingHunted(caught)) {
                    hunt.rewardCatch(envyPlayer, caught);
                    hunt.generatePokemon();
                    break;
                }
            }
        });
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPokemonCaught(CaptureEvent.SuccessfulRaidCapture event) {
        event.getPokemon().getPokemon().removeNickname();
        UtilConcurrency.runAsync(() -> {
            Pokemon caught = event.getRaidPokemon();
            ServerPlayerEntity player = event.player;
            ForgeEnvyPlayer envyPlayer = this.mod.getPlayerManager().getPlayer(player);

            for (PixelHunt hunt : PixelHuntFactory.getAllHunts()) {
                if (hunt.isBeingHunted(caught)) {
                    hunt.rewardCatch(envyPlayer, caught);
                    hunt.generatePokemon();
                    break;
                }
            }
        });
    }
}
