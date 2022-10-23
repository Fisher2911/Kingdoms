package io.github.fisher2911.kingdoms.listener;

public abstract class KListener {

    protected final GlobalListener globalListener;

    public KListener(GlobalListener globalListener) {
        this.globalListener = globalListener;
    }

    public abstract void init();
}
