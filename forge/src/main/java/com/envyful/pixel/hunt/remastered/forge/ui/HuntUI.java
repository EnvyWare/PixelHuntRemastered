package com.envyful.pixel.hunt.remastered.forge.ui;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.Page;
import ca.landonjw.gooeylibs.inventory.api.Template;
import com.envyful.pixel.hunt.remastered.forge.PixelHuntForge;
import com.envyful.pixel.hunt.remastered.api.PixelHunt;
import com.envyful.pixel.hunt.remastered.forge.hunt.PixelHuntForgeFactory;
import com.envyful.pixel.hunt.remastered.forge.utils.item.ItemBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;

public class HuntUI {

    private static final Button BACKGROUND_FILLER = Button.of(new ItemBuilder()
            .type(Item.getByNameOrId(PixelHuntForge.getInstance().getConfig().getBackgroundItem()))
            .damage(PixelHuntForge.getInstance().getConfig().getBackgroundItemDamage())
            .build());

    private static final Button BACKGROUND_OFF_FILLER = Button.of(new ItemBuilder()
            .type(Item.getByNameOrId(PixelHuntForge.getInstance().getConfig().getOffColourBackgroundItem()))
            .damage(PixelHuntForge.getInstance().getConfig().getOffColourBackgroundItemDamage())
            .build());

    public static void open(EntityPlayerMP player) {
        Template.Builder template = Template.builder(PixelHuntForge.getInstance().getConfig().getGuiHeight());

        if (PixelHuntForge.getInstance().getConfig().isCheckeredBackground()) {
            template.checker(0, 0, 8, PixelHuntForge.getInstance().getConfig().getGuiHeight() - 1, BACKGROUND_FILLER, BACKGROUND_OFF_FILLER);
        } else {
            template.fill(BACKGROUND_FILLER);
        }

        int deltaY = 0;
        int deltaX = 0;

        for (PixelHunt hunt : PixelHuntForgeFactory.getAllHunts()) {
            template.set(1 + deltaX, 1 + deltaY, Button.of(hunt.getDisplay()));

            ++deltaX;

            if (deltaX == 7) {
                deltaX = 1;
                ++deltaY;
            }
        }

        Page.builder()
                .title(PixelHuntForge.getInstance().getConfig().getGuiName())
                .template(template.build())
                .build().forceOpenPage(player);
    }
}
