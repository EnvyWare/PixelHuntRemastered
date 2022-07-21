package com.envyful.pixel.hunt.remastered.api;

import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.PokemonSpec;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.spongepowered.configurate.ConfigurationNode;

/**
 *
 * An interface to represent the different hunts available on a server
 *
 */
public interface PixelHunt {

    /**
     *
     * Loads the hunt from the {@link ConfigurationNode}
     *
     * @param config The node the hunt is being loaded from
     * @deprecated Loading now changed to use Configurate
     */
    @Deprecated
    void load(ConfigurationNode config);

    /**
     *
     * Displays the hunt on the GUI
     *
     */
    void display(Pane pane);

    /**
     *
     * Randomly generates the next pokemon for this hunt's rarity.
     *
     * @return The specification of the new pokemon to be hunted
     */
    PokemonSpec generatePokemon();

    /**
     *
     * Checks if a specific pokemon is currently the target for this hunt
     *
     * @param pokemon The target pokemon
     * @return if it's to be hunted or not
     */
    boolean isBeingHunted(Pokemon pokemon);

    /**
     *
     * Checks if a given species is currently the target for this hunt
     *
     * @param pokemon The pokemon
     * @return True if it's hunted
     */
    boolean isSpeciesHunted(Pokemon pokemon);

    /**
     *
     * Rewards the player for catching the hunted pokemon
     *
     * @param player The player who won the hunt
     */
    void rewardCatch(EnvyPlayer<?> player, Pokemon caught);

    /**
     *
     * Force end the hunt
     *
     */
    void end();

    /**
     *
     * Check if the hunt has timed out (i.e. no one caught the specified pokemon in time)
     *
     * @return true if timed out - false if not
     */
    boolean hasTimedOut();

    void spawnParticle(Object o);

    void applyNickname(Object o);
}
