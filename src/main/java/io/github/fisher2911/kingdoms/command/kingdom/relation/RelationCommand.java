package io.github.fisher2911.kingdoms.command.kingdom.relation;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationManager;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class RelationCommand extends KCommand {

    private final RelationType type;
    private final RelationManager relationManager;

    public RelationCommand(Kingdoms plugin, String name, RelationType type) {
        super(plugin, name, null, CommandSenderType.PLAYER, 1, 1, new HashMap<>());
        this.type = type;
        this.relationManager = this.plugin.getRelationManager();
    }

    public static List<RelationCommand> createAll(Kingdoms plugin) {
        return List.of(
                new RelationCommand(plugin, "enemy", RelationType.ENEMY),
                new RelationCommand(plugin, "neutral", RelationType.NEUTRAL),
                new RelationCommand(plugin, "truce", RelationType.TRUCE),
                new RelationCommand(plugin, "ally", RelationType.ALLY)
        );
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final String kingdomName = args[0];
        this.relationManager.tryAddRelation(user, kingdomName, this.type);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k [enemy | neutral | truce | ally]");
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs) {
        return super.getTabs(user, args, previousArgs);
    }
}
