package io.github.fisher2911.kingdoms.command.kingdom.relation;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationManager;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class RelationCommand extends KCommand {

    private final RelationType type;
    private final RelationManager relationManager;

    public RelationCommand(Kingdoms plugin, KCommand parent, String name, RelationType type) {
        super(plugin, parent, name, null, CommandSenderType.PLAYER, 1, 1, new HashMap<>());
        this.type = type;
        this.relationManager = this.plugin.getRelationManager();
    }

    public static List<RelationCommand> createAll(Kingdoms plugin, KCommand parent) {
        return List.of(
                new RelationCommand(plugin, parent, "enemy", RelationType.ENEMY),
                new RelationCommand(plugin, parent, "neutral", RelationType.NEUTRAL),
                new RelationCommand(plugin, parent, "truce", RelationType.TRUCE),
                new RelationCommand(plugin, parent, "ally", RelationType.ALLY)
        );
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final String kingdomName = args[0];
        TaskChain.create(this.plugin)
                .runAsync(() -> this.relationManager.tryAddRelation(user, kingdomName, this.type, true))
                .execute();
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        return super.getTabs(user, args, previousArgs, false);
    }
}
