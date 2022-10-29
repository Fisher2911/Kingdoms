package io.github.fisher2911.kingdoms.task;

import io.github.fisher2911.kingdoms.Kingdoms;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TaskChain<T, R> {

    private final Kingdoms plugin;
    private final Queue<Task> queue;

    public TaskChain(Kingdoms plugin, Queue<Task> queue) {
        this.plugin = plugin;
        this.queue = queue;
    }

    public static <T, R> TaskChain<T, R> create(Kingdoms plugin) {
        return new TaskChain<>(plugin, new ArrayDeque<>());
    }

    public <X> TaskChain<T, X> sync(Function<R, X> function) {
        return this.syncLater(function, 0);
    }

    public <X> TaskChain<T, X> supplySync(Supplier<X> supplier) {
        return this.syncLater(a -> supplier.get(), 0);
    }

    public <X> TaskChain<T, X> consumeSync(Consumer<R> supplier) {
        return this.syncLater(a -> {
            supplier.accept(a);
            return null;
        }, 0);
    }

    public <X> TaskChain<T, X> runSync(Runnable runnable) {
        return this.syncLater(a -> {
            runnable.run();
            return null;
        }, 0);
    }

    public <X> TaskChain<T, X> supplySyncLater(Supplier<X> supplier, int delay) {
        return this.syncLater(a -> supplier.get(), delay);
    }

    public <X> TaskChain<T, X> consumeSyncLater(Consumer<R> supplier, int delay) {
        return this.syncLater(a -> {
            supplier.accept(a);
            return null;
        }, delay);
    }

    public <X> TaskChain<T, X> runSyncLater(Runnable runnable, int delay) {
        return this.syncLater(a -> {
            runnable.run();
            return null;
        }, delay);
    }

    public <X> TaskChain<T, X> syncLater(Function<R, X> function, int delay) {
        this.queue.add(new Task((Function<Object, Object>) function, false, delay));
        return new TaskChain<>(this.plugin, queue);
    }

    public <X> TaskChain<T, X> async(Function<R, X> function) {
        return this.asyncLater(function, 0);
    }

    public <X> TaskChain<T, X> supplyAsync(Supplier<X> supplier) {
        return this.asyncLater(a -> supplier.get(), 0);
    }

    public <X> TaskChain<T, X> runAsync(Runnable runnable) {
        return this.asyncLater(a -> {
            runnable.run();
            return null;
        }, 0);
    }

    public <X> TaskChain<T, X> consumeAsync(Consumer<R> supplier) {
        return this.asyncLater(a -> {
            supplier.accept(a);
            return null;
        }, 0);
    }

    public <X> TaskChain<T, X> consumeAsyncLater(Consumer<R> supplier, int delay) {
        return this.asyncLater(a -> {
            supplier.accept(a);
            return null;
        }, delay);
    }

    public <X> TaskChain<T, X> supplyAsyncLater(Supplier<X> supplier, int delay) {
        return this.asyncLater(a -> supplier.get(), delay);
    }

    public <X> TaskChain<T, X> runAsyncLater(Runnable runnable, int delay) {
        return this.asyncLater(a -> {
            runnable.run();
            return null;
        }, delay);
    }

    public <X> TaskChain<T, X> asyncLater(Function<R, X> function, int delay) {
        this.queue.add(new Task((Function<Object, Object>) function, true, delay));
        return new TaskChain<>(this.plugin, queue);
    }

    public void execute() {
        this.runNext(this.queue.poll(), this.queue, null);
    }

    private void runNext(@Nullable Task current, Queue<Task> tasks, Object previousValue) {
        if (current == null) return;
        if (current.isAsync()) {
            this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin,
                    () -> runNext(tasks.poll(), tasks, current.function.apply(previousValue)), current.getDelay()
            );
            return;
        }
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin,
                () -> runNext(tasks.poll(), tasks, current.function.apply(previousValue)), current.getDelay()
        );
    }

    private record Task(Function<Object, Object> function, boolean async,
                        int delay) {

        public Function<Object, Object> getFunction() {
            return function;
        }

        public boolean isAsync() {
            return async;
        }

        public int getDelay() {
            return delay;
        }
    }

}
