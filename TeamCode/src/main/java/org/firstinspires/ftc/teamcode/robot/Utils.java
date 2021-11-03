package org.firstinspires.ftc.teamcode.robot;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Utils {
    public static boolean isEqual(double lhs, double rhs, double tolerance) {
        return Math.abs(lhs - rhs) < tolerance;
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(val, max));
    }

    public static ScheduledFuture<?> poll(ScheduledExecutorService scheduler, Supplier<Boolean> fn, Runnable onEnd, long time, TimeUnit unit) {
        AtomicBoolean endCalled = new AtomicBoolean();

        ScheduledFuture<?> f = scheduler.scheduleAtFixedRate(() -> {
            if (!fn.get()) {
                return;
            }
            if (onEnd != null && endCalled.compareAndSet(false, true)) {
                onEnd.run();
            }
            throw new RuntimeException();
        }, 0, time, unit);

        return new ScheduledFuture<Object>() {
            @Override
            public long getDelay(TimeUnit unit) {
                return f.getDelay(unit);
            }

            @Override
            public int compareTo(Delayed o) {
                return f.compareTo(o);
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (!f.cancel(mayInterruptIfRunning)) {
                    return false;
                }

                if (endCalled.compareAndSet(false, true)) {
                    onEnd.run();
                }

                return true;
            }

            @Override
            public boolean isCancelled() {
                return f.isCancelled();
            }

            @Override
            public boolean isDone() {
                return f.isDone();
            }

            @Override
            public Object get() throws ExecutionException, InterruptedException {
                return f.get();
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
                return f.get(timeout, unit);
            }
        };
    }
}
