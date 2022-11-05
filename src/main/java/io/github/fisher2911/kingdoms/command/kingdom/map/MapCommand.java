package io.github.fisher2911.kingdoms.command.kingdom.map;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import org.jetbrains.annotations.Nullable;
import io.github.fisher2911.kingdoms.kingdom.claim.MapVisualizer;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;

public class MapCommand extends KCommand {

    private final MapVisualizer mapVisualizer;

    public MapCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "map", null, CommandSenderType.PLAYER, 0, 0, subCommands);
        this.mapVisualizer = this.plugin.getMapVisualizer();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.mapVisualizer.show(user);
    }

}
