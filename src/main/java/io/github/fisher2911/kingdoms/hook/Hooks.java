package io.github.fisher2911.kingdoms.hook;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.kingdoms.hook.claim.ClaimHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Hooks {

    private final Map<String, Hook> hooks = new HashMap<>();
    private final Multimap<HookType, Hook> hookTypeMap = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

    public Hooks() {}

    public void load() {
        this.registerHooks();
        for (Hook hook : this.hooks.values()) {
            hook.onLoad();
        }
    }

    private void registerHooks() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            registerHook(new WorldGuardHook());
        }
    }

    private void registerHook(Hook hook) {
        this.hooks.put(hook.getId(), hook);
        for (HookType hookType : hook.getHookTypes()) {
            this.hookTypeMap.put(hookType, hook);
        }
    }

    public boolean canClaimAt(Location location) {
        for (Hook hook : this.hookTypeMap.get(HookType.CLAIM)) {
            if (!((ClaimHook) hook).canClaim(location)) {
                return false;
            }
        }
        return true;
    }

}
