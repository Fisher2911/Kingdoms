package io.github.fisher2911.kingdoms.command.kingdom.upgrade;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import org.jetbrains.annotations.Nullable;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.gui.GuiManager;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Map;
import java.util.Set;

public class UpgradeCommand extends KCommand {

    private final KingdomManager kingdomManager;

    public UpgradeCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "upgrades", null, CommandSenderType.PLAYER, 0, 1, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    this.plugin.getGuiManager().open(
                            GuiManager.UPGRADES_GUI,
                            user,
                            Map.of(
                                    GuiKeys.USER, user,
                                    GuiKeys.KINGDOM, kingdom
                            ),
                            Set.of()
                    );
                }, () -> MessageHandler.sendNotInKingdom(user)))
                .execute();
    }

}
