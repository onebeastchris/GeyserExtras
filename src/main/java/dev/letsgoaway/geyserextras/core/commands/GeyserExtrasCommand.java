package dev.letsgoaway.geyserextras.core.commands;

import dev.letsgoaway.geyserextras.core.Config;
import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.menus.MainMenu;

import java.util.List;

public class GeyserExtrasCommand implements BedrockCommand {

    @Override
    public void onExecute(ExtrasPlayer player, List<String> args) {
        if (!args.isEmpty()) {
            String subcommand = args.get(0);
            if (subcommand.equalsIgnoreCase("reload")) {
                Config.load();
                return;
            }
        }
        player.sendForm(new MainMenu());
    }
}
