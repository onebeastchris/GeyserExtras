package dev.letsgoaway.geyserextras.core.handlers.java;

import dev.letsgoaway.geyserextras.core.Config;
import dev.letsgoaway.geyserextras.core.SoundReplacer;
import org.cloudburstmc.protocol.bedrock.packet.PlaySoundPacket;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.protocol.PacketTranslator;
import org.geysermc.geyser.translator.protocol.Translator;
import org.geysermc.geyser.translator.protocol.java.entity.JavaSoundEntityTranslator;
import org.geysermc.geyser.util.SoundUtils;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSoundEntityPacket;

@Translator(packet = ClientboundSoundEntityPacket.class)
public class JavaSoundEntityInjector extends PacketTranslator<ClientboundSoundEntityPacket> {
    JavaSoundEntityTranslator translator = new JavaSoundEntityTranslator();

    @Override
    public void translate(GeyserSession session, ClientboundSoundEntityPacket packet) {
        if (!Config.javaCombatSounds) {
            translator.translate(session, packet);
            return;
        }
        Entity entity = session.getEntityCache().getEntityByJavaId(packet.getEntityId());
        if (entity == null) {
            return;
        }

        if (SoundReplacer.soundMap.containsKey(packet.getSound().getName())) {
            PlaySoundPacket playSoundPacket = new PlaySoundPacket();
            playSoundPacket.setSound(SoundReplacer.getSound(packet.getSound().getName()));
            playSoundPacket.setPosition(entity.getPosition());
            playSoundPacket.setVolume(packet.getVolume());
            playSoundPacket.setPitch(packet.getPitch());
            session.sendUpstreamPacket(playSoundPacket);
        }
        else {
            SoundUtils.playSound(session, packet.getSound(), entity.getPosition(), packet.getVolume(), packet.getPitch());
        }

    }
}
