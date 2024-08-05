package dev.letsgoaway.geyserextras;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;

public interface TickUtil {
    void runIn(long ticks, Runnable func, ExtrasPlayer player);

    void runSync(Runnable func, ExtrasPlayer player);
}
