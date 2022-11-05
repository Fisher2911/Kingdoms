package io.github.fisher2911.kingdoms.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import io.github.fisher2911.kingdoms.hook.claim.ClaimHook;
import org.bukkit.Location;

import java.util.List;

public class WorldGuardHook extends Hook implements ClaimHook {

    private static final String ID = "WorldGuard";
    private static final BooleanFlag UNCLAIMABLE_FLAG = new BooleanFlag("unclaimable");

    public WorldGuardHook() {
        super(ID, List.of(HookType.CLAIM));
    }

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        registry.register(UNCLAIMABLE_FLAG);
    }

    @Override
    public boolean canClaim(Location at) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(at));
        final Boolean value = set.queryValue(null, UNCLAIMABLE_FLAG);
        return value == null || !value;
    }

}
