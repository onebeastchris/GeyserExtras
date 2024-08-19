package dev.letsgoaway.geyserextras.core.handlers.bedrock;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.preferences.bindings.Remappable;
import dev.letsgoaway.geyserextras.core.handlers.GeyserHandler;
import org.cloudburstmc.protocol.bedrock.packet.ServerSettingsRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ServerSettingsResponsePacket;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.impl.FormDefinitions;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.protocol.bedrock.BedrockServerSettingsRequestTranslator;

import java.util.concurrent.TimeUnit;

public class BedrockServerSettingsRequestInjector extends BedrockServerSettingsRequestTranslator {
    @Override
    public void translate(GeyserSession session, ServerSettingsRequestPacket packet) {
        ExtrasPlayer player = GeyserHandler.getPlayer(session);
        player.getPreferences().runAction(Remappable.SETTINGS);
        CustomForm form = GeyserHandler.getPlayer(session).getPreferences().getSettingsMenuForm().open(GeyserHandler.getPlayer(session));
        int formId = session.getFormCache().addForm(form);

        String jsonData = FormDefinitions.instance().codecFor(form).jsonData(form);

        // Fixes https://bugs.mojang.com/browse/MCPE-94012 because of the delay
        session.scheduleInEventLoop(() -> {
            ServerSettingsResponsePacket serverSettingsResponsePacket = new ServerSettingsResponsePacket();
            serverSettingsResponsePacket.setFormData(jsonData);
            serverSettingsResponsePacket.setFormId(formId);
            session.sendUpstreamPacket(serverSettingsResponsePacket);
        }, 1, TimeUnit.SECONDS);
    }
}
