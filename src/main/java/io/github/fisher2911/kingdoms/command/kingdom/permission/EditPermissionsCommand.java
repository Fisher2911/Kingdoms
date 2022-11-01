package io.github.fisher2911.kingdoms.command.kingdom.permission;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.gui.GuiManager;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditPermissionsCommand extends KCommand {

    private final RoleManager roleManager;
    private final KingdomManager kingdomManager;
    private final GuiManager guiManager;

    public EditPermissionsCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "edit", null, CommandSenderType.PLAYER, 1, -1, subCommands);
        this.roleManager = this.plugin.getRoleManager();
        this.kingdomManager = this.plugin.getKingdomManager();
        this.guiManager = this.plugin.getGuiManager();
        this.addSubCommand(new EditChunkPermissionsCommand(this.plugin, new HashMap<>()));
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom ->
                        this.guiManager.open(
                                GuiManager.PERMISSIONS_GUI,
                                user,
                                Map.of(
                                        GuiKeys.USER, user,
                                        GuiKeys.KINGDOM, kingdom,
                                        GuiKeys.ROLE_ID, args[0]
                                ),
                                Set.of()
                        ), () -> MessageHandler.sendNotInKingdom(user)))
                .execute();
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/k permission edit [role]");
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        List<String> tabs = super.getTabs(user, args, previousArgs, false);
        if (tabs == null) tabs = new ArrayList<>();
        final String lastArg = previousArgs[previousArgs.length - 1];
        if (!lastArg.equalsIgnoreCase("edit")) return tabs;
        if (args.length != 1) return tabs;
        final String arg = args[0];
        for (String role : this.roleManager.getAllRoleIds()) {
            if (!role.equals(this.roleManager.getLeaderRoleId()) && role.startsWith(arg)) tabs.add(role);
        }
        return tabs;
    }
}
