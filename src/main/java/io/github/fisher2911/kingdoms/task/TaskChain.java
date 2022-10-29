package io.github.fisher2911.kingdoms.task;

import io.github.fisher2911.kingdoms.Kingdoms;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Function;

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

    public <X> TaskChain<T, X> syncLater(Function<R, X> function, int delay) {
        this.queue.add(new Task((Function<Object, Object>) function, false, delay));
        return new TaskChain<>(this.plugin, queue);
    }

    public <X> TaskChain<T, X> async(Function<R, X> function) {
        return this.asyncLater(function, 0);
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
