package io.github.fisher2911.kingdoms.config.serializer;

public class PermissionItemSerializer {

//    public static final PermissionItemSerializer INSTANCE = new PermissionItemSerializer();
//
//    private PermissionItemSerializer() {
//    }
//
//    private static final Consumer<InventoryEventWrapper<InventoryClickEvent>> PERMISSION_SWAP_VALUE_CLICK_ACTION =
//            wrapper -> {
//                final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
//                final UserManager userManager = plugin.getUserManager();
//                final InventoryClickEvent event = wrapper.event();
//                final BaseGui gui = wrapper.gui();
//                final int slot = event.getSlot();
//                final BaseGuiItem clicked = gui.getItem(slot);
//                if (clicked == null) return;
//                final KPermission permission = clicked.getMetadata(GuiKeys.PERMISSION, KPermission.class);
//                if (permission == null) return;
//                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
//                final Role role = gui.getMetadata(GuiKeys.ROLE, Role.class);
//                final ClaimedChunk chunk = gui.getMetadata(GuiKeys.CHUNK, ClaimedChunk.class);
//                event.setCancelled(true);
//                final User user = userManager.wrap(event.getWhoClicked());
//                final Role userRole = kingdom.getRole(user);
//                Bukkit.broadcastMessage("userRole: " + userRole + " role: " + role + " (" + role.isAtLeastRank(userRole) + ")");
//                if (userRole == null || role.isAtLeastRank(userRole)) {
//                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
//                    return;
//                }
//                if ((chunk != null && !kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS, chunk)) &&
//                        (!kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS))) {
//                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
//                    return;
//                }
//
//                if (chunk != null && !kingdom.hasPermission(user, permission, chunk) || (chunk == null && !kingdom.hasPermission(user, permission))) {
//                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
//                    return;
//                }
//                final boolean newValue = chunk == null ? !kingdom.hasPermission(role, permission) : !kingdom.hasPermission(role, permission, chunk);
//                final RolePermissionHolder permissions;
//                permissions = Objects.requireNonNullElse(chunk, kingdom);
//                permissions.setPermission(role, permission, newValue);
//                gui.refresh(slot);
//            };
//
//    public static void applyPermissionItemData(GuiItem.Builder builder, String permissionId) {
//        final Map<Object, Object> metadata = new HashMap<>();
//        metadata.put(GuiKeys.SWAP_VALUE_CONSUMER, PERMISSION_SWAP_VALUE_CLICK_ACTION);
//        builder.metadata(metadata);
//        final KPermission permission = KPermission.get(permissionId);
//        if (permission == null) return;
//        metadata.put(GuiKeys.PERMISSION, permission);
//        builder.metadata(metadata);
//        builder.placeholder((gui, item) -> {
//            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
//            if (kingdom == null) return false;
//            final Role role = gui.getMetadata(GuiKeys.ROLE, Role.class);
//            if (role == null) return false;
//            final ClaimedChunk chunk = gui.getMetadata(GuiKeys.CHUNK, ClaimedChunk.class);
//            if (chunk == null) {
//                return new PermissionWrapper(permission, kingdom.hasPermission(role, permission));
//            }
//            return new PermissionWrapper(permission, kingdom.hasPermission(role, permission, chunk));
//        });
//    }

}
