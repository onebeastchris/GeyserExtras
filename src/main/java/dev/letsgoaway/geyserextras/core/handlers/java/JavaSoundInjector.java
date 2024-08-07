package dev.letsgoaway.geyserextras.core.handlers.java;

import dev.letsgoaway.geyserextras.core.Config;
import dev.letsgoaway.geyserextras.core.SoundReplacer;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.PlaySoundPacket;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.protocol.PacketTranslator;
import org.geysermc.geyser.translator.protocol.Translator;
import org.geysermc.geyser.translator.protocol.java.level.JavaSoundTranslator;
import org.geysermc.geyser.util.SoundUtils;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.Sound;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundSoundPacket;
@Translator(packet = ClientboundSoundPacket.class)
public class JavaSoundInjector extends JavaSoundTranslator {
    @Override
    public void translate(GeyserSession session, ClientboundSoundPacket packet) {
        if (!Config.javaCombatSounds) {
            super.translate(session, packet);
            return;
        }
        Vector3f position = Vector3f.from(packet.getX(), packet.getY(), packet.getZ());

        if (SoundReplacer.soundMap.containsKey(packet.getSound().getName())) {
            PlaySoundPacket playSoundPacket = new PlaySoundPacket();
            playSoundPacket.setSound(SoundReplacer.getSound(packet.getSound().getName()));
            playSoundPacket.setPosition(position);
            playSoundPacket.setVolume(packet.getVolume());
            playSoundPacket.setPitch(packet.getPitch());
            session.sendUpstreamPacket(playSoundPacket);
        }
        else {
            SoundUtils.playSound(session, packet.getSound(), position, packet.getVolume(), packet.getPitch());
        }
    }
}