package com.envyful.pixel.hunt.remastered.forge.commands;


import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.pixel.hunt.remastered.forge.PixelHuntForge;
import com.envyful.pixel.hunt.remastered.forge.ui.HuntUI;
import net.minecraft.entity.player.ServerPlayerEntity;

@Command(
       value = "hunt",
        description = "A command to open the PixelHunt UI",
        aliases = {
               "pixelhunt",
               "pokemonhunt",
               "pokehunt",
                "hunts",
                "pixelhunts",
                "pokemonhunts",
                "pokehunts"
        }
)
@SubCommands(ReloadCommand.class)
public class PixelHuntCommand {

    @CommandProcessor
    public void executeCommand(@Sender ServerPlayerEntity sender) {
        UtilForgeConcurrency.runSync(() ->
                HuntUI.open(PixelHuntForge.getInstance().getPlayerManager().getPlayer(sender)));
    }
}

