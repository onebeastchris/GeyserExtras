package dev.letsgoaway.geyserextras.core.menus;

import dev.letsgoaway.geyserextras.core.preferences.bindings.Action;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.elements.Button;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.locale.BedrockLocale;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

public class MainMenu extends BedrockMenu {
    public MainMenu() {
        super();
    }

    @Override
    public SimpleForm create(ExtrasPlayer player) {
        setTitle("GeyserExtras Menu");
        add(new Button(Action.SWAP_OFFHAND.translate(player), FormImage.Type.PATH, "textures/ui/move.png", () -> {
            Action.SWAP_OFFHAND.run(player);
        }));

        add(new Button(Action.RECONNECT.translate(player), FormImage.Type.PATH, "textures/ui/refresh_hover.png", () -> {
            Action.RECONNECT.run(player);
        }));

        add(new Button(Action.TOGGLE_TOOLTIPS.translate(player), FormImage.Type.PATH, "textures/ui/infobulb.png", () -> {
            Action.TOGGLE_TOOLTIPS.run(player);
        }));

        add(new Button(Action.OPEN_ADVANCEMENTS.translate(player), FormImage.Type.PATH, "textures/ui/achievements.png", () -> {
            Action.OPEN_ADVANCEMENTS.run(player);
        }));

        add(new Button(Action.OPEN_STATISTICS.translate(player), FormImage.Type.PATH, "textures/ui/world_glyph_color_2x_black_outline.png", () -> {
            Action.OPEN_STATISTICS.run(player);
        }));

        add(new Button(Action.PLAYER_LIST.translate(player), FormImage.Type.PATH, "textures/ui/Local.png", () -> {
            Action.PLAYER_LIST.run(player);
        }));


        add(new Button(BedrockLocale.SETTINGS, FormImage.Type.PATH, "textures/ui/settings_glyph_color_2x.png", () -> {
            player.sendForm(new SettingsMenu());
        }));


        add(new Button(BedrockLocale.MENU.RESOURCE_PACKS, FormImage.Type.PATH, "textures/ui/glyph_resource_pack.png", () -> {
            // TODO
            //new OptionalPacks(bplayer).show(bplayer);
        }));
        return super.create(player);
    }
}
