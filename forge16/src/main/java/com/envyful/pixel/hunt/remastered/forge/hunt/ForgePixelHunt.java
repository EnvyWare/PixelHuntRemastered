package com.envyful.pixel.hunt.remastered.forge.hunt;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.PokemonGenerator;
import com.envyful.api.reforged.pixelmon.PokemonSpec;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.pixel.hunt.remastered.api.PixelHunt;
import com.envyful.pixel.hunt.remastered.forge.config.PixelHuntConfig;
import com.envyful.pixel.hunt.remastered.forge.event.PixelHuntStartEvent;
import com.envyful.pixel.hunt.remastered.forge.event.PixelHuntWonEvent;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.concurrent.TimeUnit;

public class ForgePixelHunt implements PixelHunt {

    private final PixelHuntConfig.HuntConfig huntConfig;
    private PokemonGenerator generator;
    private ItemStack displayItem;
    private PokemonSpec currentPokemon;
    private long duration;
    private long currentStart;

    public ForgePixelHunt(PixelHuntConfig.HuntConfig huntConfig) {
        this.huntConfig = huntConfig;
        this.generator = new PokemonGenerator(huntConfig.getGeneratorConfig());
        this.duration = TimeUnit.MINUTES.toMillis(huntConfig.getMaxDurationMinutes());
    }

    @Override
    public void load(ConfigurationNode config) {}

    @Override
    public void display(Pane pane) {
        ItemBuilder builder = new ItemBuilder(this.displayItem.copy());

        builder.name(UtilChatColour.colour(this.huntConfig.getDisplayName().replace("%species%", this.currentPokemon.getDisplayName())));

        for (String s : this.huntConfig.getPreLore()) {
            builder.addLore(UtilChatColour.colour(s.replace("%time%",
                    UtilTimeFormat.getFormattedDuration((this.currentStart + this.duration) - System.currentTimeMillis()))));
        }

        for (String s : this.currentPokemon.getDescription(this.huntConfig.getDescriptionColour(), this.huntConfig.getDescriptionOffColour())) {
            builder.addLore(UtilChatColour.colour(s));
        }

        for (String s : this.huntConfig.getExtraLore()) {
            builder.addLore(UtilChatColour.colour(s.replace("%time%",
                    UtilTimeFormat.getFormattedDuration((this.currentStart + this.duration) - System.currentTimeMillis()))));
        }

        for (String s : this.huntConfig.getRewardDescription()) {
            builder.addLore(UtilChatColour.colour(s));
        }

        pane.set(this.huntConfig.getGuiX(), this.huntConfig.getGuiY(), GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(builder.build()).build());
    }

    @Override
    public PokemonSpec generatePokemon() {
        this.currentPokemon = this.generator.generate();
        this.displayItem = this.currentPokemon.getPhoto();
        PixelHuntStartEvent event = new PixelHuntStartEvent(this, this.currentPokemon);

        Pixelmon.EVENT_BUS.post(event);

        this.currentStart = System.currentTimeMillis();
        this.currentPokemon = event.getGeneratedPokemon();

        for (String broadcast : this.huntConfig.getSpawnBroadcast()) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(
                    UtilChatColour.colour(broadcast.replace("%pokemon%", this.currentPokemon.getDisplayName())),
                    ChatType.CHAT, Util.NIL_UUID
            );
        }

        return this.currentPokemon;
    }

    @Override
    public boolean isBeingHunted(Pokemon pokemon) {
        if (this.currentPokemon == null || pokemon == null) {
            return false;
        }

        return this.currentPokemon.matches(pokemon);
    }

    @Override
    public boolean isSpeciesHunted(Pokemon pokemon) {
        if (this.currentPokemon == null || pokemon == null) {
            return false;
        }

        return this.currentPokemon.doesSpeciesMatch(pokemon);
    }

    @Override
    public void rewardCatch(EnvyPlayer<?> player, Pokemon caught) {
        ServerPlayerEntity parent = (ServerPlayerEntity) player.getParent();
        PixelHuntWonEvent wonEvent = new PixelHuntWonEvent(this, parent, caught, this.currentPokemon);

        Pixelmon.EVENT_BUS.post(wonEvent);

        if (this.huntConfig.isIvMultiplierEnabled()) {
            for (BattleStatsType value : BattleStatsType.values()) {
                if (value == BattleStatsType.NONE || value == BattleStatsType.ACCURACY || value == BattleStatsType.EVASION) {
                    continue;
                }

                caught.getIVs().setStat(value, Math.min(31, (int) (caught.getIVs().getStat(value) * this.huntConfig.getIvMultiplier())));
            }
        } else if (this.huntConfig.isMaxIvs()) {
            for (BattleStatsType value : BattleStatsType.values()) {
                if (value == BattleStatsType.NONE || value == BattleStatsType.ACCURACY || value == BattleStatsType.EVASION) {
                    continue;
                }

                caught.getIVs().setStat(value, 31);
            }
        }

        for (String broadcast : this.huntConfig.getRewardBroadcast()) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(
                    UtilChatColour.colour(broadcast
                            .replace("%pokemon%", this.currentPokemon.getDisplayName())
                            .replace("%player%", parent.getName().getString())), ChatType.CHAT, Util.NIL_UUID
            );
        }

        UtilForgeConcurrency.runSync(() -> {
            if (this.huntConfig.isRandomCommands()) {
                UtilForgeServer.executeCommand(UtilRandom.getRandomElement(this.huntConfig.getRewardCommands())
                        .replace("%player%", parent.getName().getString()));
            } else {
                for (String rewardCommand : this.huntConfig.getRewardCommands()) {
                    UtilForgeServer.executeCommand(rewardCommand.replace("%player%", parent.getName().getString()));
                }
            }
        });
    }

    @Override
    public void end() {
        if (this.currentPokemon == null) {
            return;
        }

        for (String message : this.huntConfig.getTimeoutBroadcast()) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(
                    UtilChatColour.colour(message.replace("%pokemon%", this.currentPokemon.getDisplayName())),
                    ChatType.CHAT, Util.NIL_UUID
            );
        }
    }

    @Override
    public boolean hasTimedOut() {
        long timePassed = System.currentTimeMillis() - this.currentStart;

        return timePassed >= this.duration;
    }

    @Override
    public void spawnParticle(Object o) {
        if (!(o instanceof PixelmonEntity)) {
            return;
        }

        PixelmonEntity pixelmon = (PixelmonEntity) o;

        pixelmon.level.addParticle(this.huntConfig.getParticles(), pixelmon.getX(), pixelmon.getY(), pixelmon.getZ(),
                5, 0, 0.05);
    }

    @Override
    public void applyNickname(Object o) {
        if (!(o instanceof PixelmonEntity)) {
            return;
        }

        PixelmonEntity pixelmon = (PixelmonEntity) o;
        if (pixelmon.hasOwner()) {
            return;
        }

        ITextComponent nickname = UtilChatColour.colour(this.huntConfig.getPokemonNickname().replace("%species%",
                pixelmon.getLocalizedName()));
        pixelmon.getPokemon().setNickname(nickname);
    }
}
