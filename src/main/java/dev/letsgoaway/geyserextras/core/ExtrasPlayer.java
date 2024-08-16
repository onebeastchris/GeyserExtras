package dev.letsgoaway.geyserextras.core;

import dev.letsgoaway.geyserextras.core.features.bindings.Remappable;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.BedrockForm;
import dev.letsgoaway.geyserextras.core.form.BedrockModal;
import dev.letsgoaway.geyserextras.core.parity.java.combat.CooldownHandler;
import dev.letsgoaway.geyserextras.core.parity.java.shield.ShieldUtils;
import dev.letsgoaway.geyserextras.core.parity.java.tablist.TabListData;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;
import org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket;
import org.geysermc.api.util.BedrockPlatform;
import org.geysermc.api.util.InputMode;
import org.geysermc.geyser.api.bedrock.camera.GuiElement;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.event.bedrock.ClientEmoteEvent;
import org.geysermc.geyser.session.GeyserSession;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static dev.letsgoaway.geyserextras.core.GeyserExtras.SERVER;

public class ExtrasPlayer {
    @Getter
    private UUID javaUUID;

    @Getter
    private String bedrockXUID;

    @Setter
    private List<UUID> emotesList;

    @Getter
    public GeyserSession session;

    @Getter
    private final CooldownHandler cooldownHandler;

    @Getter
    private final TabListData tabListData;

    @Getter
    private final PreferencesData preferences;

    @Getter
    private ScheduledFuture<?> combatTickThread;

    @Getter
    @Setter
    private ScheduledFuture<?> vrInventoryMenuFuture;

    public ExtrasPlayer(GeyserConnection connection) {
        this.session = (GeyserSession) connection;
        this.javaUUID = connection.javaUuid();
        this.bedrockXUID = connection.xuid();
        cooldownHandler = new CooldownHandler(this);
        tabListData = new TabListData(this);
        preferences = new PreferencesData(this);
        emotesList = List.of();
        // Update the cooldown at a faster rate for smoother animations at fast periods
        startCombatTickThread(60f);
    }

    public void startGame() {
    }

    public void startCombatTickThread(float updateRate) {
        getPreferences().setIndicatorUpdateRate(updateRate);
        if (combatTickThread != null) {
            combatTickThread.cancel(false);
        }
        combatTickThread = session.getEventLoop().scheduleAtFixedRate(() -> {
            if (Config.customCoolDownEnabled) {
                getCooldownHandler().tick();
            }
        }, TickMath.toNanos(updateRate), TickMath.toNanos(updateRate), TimeUnit.NANOSECONDS);
    }

    public void onDisconnect() {
        combatTickThread.cancel(true);
        combatTickThread = null;
    }

    public void reconnect() {
        String[] data = session.getClientData().getServerAddress().split(":");
        String address = data[0];
        int port = Integer.parseInt(data[1]);
        session.transfer(address, port);
    }

    public void onEmoteEvent(ClientEmoteEvent ev) {
        int id = emotesList.indexOf(UUID.fromString(ev.emoteId()));

        if (id == -1) {
            // TODO: debug logs??? we could check if geyser has debug mode on in config
            SERVER.warn("Emote with id: " + ev.emoteId() + " was not in emote list!");
            return;
        }

        preferences.getAction(Remappable.values()[id]).run(this);
    }

    @Setter
    @Getter
    public float tickrate = 20.0f;

    public long ticks = 0;

    // Used for the VR Quick-Menu double click action
    @Setter
    @Getter
    private float lastInventoryClickTime = 0;

    public void tick() {
        ticks++;
        if (Config.disablePaperDoll) {
            session.camera().hideElement(GuiElement.PAPER_DOLL);
        }
        if (Config.toggleBlock) {
            ShieldUtils.updateBlockSpeed(session);
        }
    }

    // TODO: better way to detect instead of using identifier
    public boolean isTool() {
        String item = session.getPlayerInventory().getItemInHand().getMapping(session).getBedrockIdentifier();
        return (item.contains("_axe") || item.contains("_pickaxe") || item.contains("_shovel") || item.contains("_sword") || item.contains("trident") || item.contains("mace")
                // || item.contains("_hoe")
                // hoes dont have attack speed for some reason
        );
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        SetTitlePacket timesPacket = new SetTitlePacket();
        timesPacket.setText("");
        timesPacket.setType(SetTitlePacket.Type.TIMES);
        timesPacket.setFadeInTime(fadeIn);
        timesPacket.setStayTime(stay);
        timesPacket.setFadeOutTime(fadeOut);
        timesPacket.setXuid("");
        timesPacket.setPlatformOnlineId("");
        session.sendUpstreamPacket(timesPacket);
        SetTitlePacket titlePacket = new SetTitlePacket();
        titlePacket.setType(SetTitlePacket.Type.TITLE);
        titlePacket.setText(title.isEmpty() ? " " : title);
        titlePacket.setXuid("");
        titlePacket.setPlatformOnlineId("");
        session.sendUpstreamPacket(titlePacket);
        SetTitlePacket subtitlePacket = new SetTitlePacket();
        subtitlePacket.setType(SetTitlePacket.Type.SUBTITLE);
        subtitlePacket.setText(subtitle);
        subtitlePacket.setXuid("");
        subtitlePacket.setPlatformOnlineId("");
        session.sendUpstreamPacket(subtitlePacket);
    }

    public void sendActionbarTitle(String title) {
        SetTitlePacket titlePacket = new SetTitlePacket();
        titlePacket.setType(SetTitlePacket.Type.ACTIONBAR);
        titlePacket.setText(title);
        titlePacket.setXuid("");
        titlePacket.setPlatformOnlineId("");
        session.sendUpstreamPacket(titlePacket);
    }


    public void resetTitle() {
        SetTitlePacket titlePacket = new SetTitlePacket();
        titlePacket.setType(SetTitlePacket.Type.CLEAR);
        titlePacket.setText("");
        titlePacket.setXuid("");
        titlePacket.setPlatformOnlineId("");
        session.sendUpstreamPacket(titlePacket);
    }

    public void sendForm(BedrockForm form) {
        session.sendForm(form.create(this).build());
    }

    public void sendForm(BedrockMenu form) {
        session.sendForm(form.create(this));
    }

    public void sendForm(BedrockModal form) {
        session.sendForm(form.create(this));
    }

    public void setTickingState(float tickrate) {
        this.tickrate = tickrate;
    }

    private static final List<BedrockPlatform> vrPlatforms = List.of(BedrockPlatform.GEARVR, BedrockPlatform.HOLOLENS);

    public boolean isVR() {
        return session.getClientData().getCurrentInputMode() == org.geysermc.floodgate.util.InputMode.VR
                || session.inputMode() == InputMode.VR || vrPlatforms.contains(session.platform());
    }

    public void swingArm() {
        AnimatePacket animatePacket = new AnimatePacket();
        animatePacket.setRuntimeEntityId(session.getPlayerEntity().getGeyserId());
        animatePacket.setAction(AnimatePacket.Action.SWING_ARM);
        session.sendUpstreamPacket(animatePacket);
    }
}
