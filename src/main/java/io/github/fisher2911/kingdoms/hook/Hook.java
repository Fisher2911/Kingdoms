package io.github.fisher2911.kingdoms.hook;

import java.util.Collection;

public abstract class Hook {

    protected final String id;
    protected final Collection<HookType> hookTypes;

    public Hook(String id, Collection<HookType> hookTypes) {
        this.id = id;
        this.hookTypes = hookTypes;
    }

    public abstract void onLoad();

    public String getId() {
        return id;
    }

    public Collection<HookType> getHookTypes() {
        return hookTypes;
    }
}
