package dev.letsgoaway.geyserextras.velocity;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.TickMath;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.concurrent.TimeUnit;

public class VelocityExtrasPlayer extends ExtrasPlayer {
    Scheduler.TaskBuilder builder;
    ScheduledTask task;

    public VelocityExtrasPlayer(GeyserConnection connection) {
        super(connection);
        builder = GeyserExtrasVelocity.server.getScheduler().buildTask(GeyserExtrasVelocity.VELOCITY, this::tick).repeat(TickMath.toNanos(tickrate), TimeUnit.NANOSECONDS);
        task = builder.schedule();
    }

    @Override
    public void setTickingState(float tickrate) {
        super.setTickingState(tickrate);
        task.cancel();
        builder.clearRepeat();
        builder.repeat(TickMath.toNanos(tickrate), TimeUnit.NANOSECONDS);
        task = builder.schedule();
    }
}