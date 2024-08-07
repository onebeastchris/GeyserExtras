package dev.letsgoaway.geyserextras.extension;

import dev.letsgoaway.geyserextras.TickUtil;
import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.TickMath;

import java.util.concurrent.TimeUnit;

public class ExtensionTickUtil implements TickUtil {
    @Override
    public void runIn(long ticks, Runnable func, ExtrasPlayer player) {
        player.getSession().scheduleInEventLoop(func, TickMath.toNanos(player.getTickrate()) * ticks, TimeUnit.NANOSECONDS);
    }

    @Override
    public void runSync(Runnable func, ExtrasPlayer player) {
        player.getSession().getEventLoop().schedule(func, 0, TimeUnit.NANOSECONDS);
    }
}