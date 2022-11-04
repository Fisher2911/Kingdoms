package io.github.fisher2911.kingdoms.command.help;

import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class CommandHelpUtil {

//    private final Map<String, CommandHelp> commandHelpMap;
//    private final List<CommandHelp> commandHelpOrder;
//    private int commandsPerPage;

//    public CommandHelpManager(Kingdoms plugin) {
//        super(plugin, "command-help");
//        this.commandHelpMap = new HashMap<>();
//        this.commandHelpOrder = new ArrayList<>();
//    }

    private CommandHelpUtil() {
    }

    private static List<CommandHelp> getPageDisplay(
            User user,
            int page,
            List<CommandHelp> helpList,
            int commandsPerPage
    ) {
        final List<CommandHelp> newList = helpList
                .stream()
                .filter(help -> hasPermission(help, user))
                .collect(Collectors.toList());
        if (newList.isEmpty()) return newList;
        if (page * commandsPerPage > newList.size()) {
            page = newList.size() - commandsPerPage;
        }
        return newList.subList(page * commandsPerPage, Math.min(newList.size(), page * commandsPerPage + commandsPerPage));
    }

    public static void sendCommandHelp(
            User user,
            int page,
            List<CommandHelp> helpList,
            int commandsPerPage
    ) {
        page = Math.max(0, page);
        page = Math.min(page, helpList.size() / commandsPerPage);
        final boolean canSeeAdminInfo = user.hasPermission(CommandPermission.VIEW_ADMIN_COMMAND_HELP);
        final CommandInfo info = new CommandInfo(page - 1, page + 1);
        final Message headerMessage = canSeeAdminInfo ? Message.ADMIN_COMMAND_HELP_HEADER : Message.COMMAND_HELP_HEADER;
        MessageHandler.sendMessage(
                user,
                headerMessage,
                info
        );
        final Message infoMessage = canSeeAdminInfo ? Message.ADMIN_COMMAND_HELP_FORMAT : Message.COMMAND_HELP_FORMAT;
        getPageDisplay(user, page, helpList, commandsPerPage).forEach(help -> MessageHandler.sendMessage(
                user,
                infoMessage,
                help
        ));
        final Message footerMessage = canSeeAdminInfo ? Message.ADMIN_COMMAND_HELP_FOOTER : Message.COMMAND_HELP_FOOTER;
        MessageHandler.sendMessage(
                user,
                footerMessage,
                info
        );
    }


    private static boolean hasPermission(CommandHelp help, User user) {
        final String permission = help.getPermission();
        return permission == null || user.hasPermission(permission);
    }

//    public void addCommandHelp(String command, String usage, String permission) {
//        this.commandHelpMap.put(command, new CommandHelp(command, usage, permission));
//    }
//
//    private static final String COMMANDS_PER_PAGE_PATH = "commands-per-page";
//    private static final String COMMANDS_PATH = "commands";
//    private static final String NAME_PATH = "name";
//    private static final String USAGE_PATH = "usage";
//    private static final String PERMISSION_PATH = "permission";
//
//    public void load() {
//        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
//                .path(this.path)
//                .build();
//        try {
//            final var source = loader.load();
//            this.commandsPerPage = source.node(COMMANDS_PER_PAGE_PATH).getInt(5);
//
//            for (var entry : source.node(COMMANDS_PATH).childrenMap().entrySet()) {
//                final var node = entry.getValue();
//                final var command = node.node(NAME_PATH).getString();
//                final var usage = node.node(USAGE_PATH).getString();
//                final var permission = node.node(PERMISSION_PATH).getString();
//                this.addCommandHelp(command, usage, permission);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
