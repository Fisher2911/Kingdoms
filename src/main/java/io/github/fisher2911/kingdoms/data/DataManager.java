package io.github.fisher2911.kingdoms.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.data.sql.SQLObject;
import io.github.fisher2911.kingdoms.data.sql.SQLType;
import io.github.fisher2911.kingdoms.data.sql.condition.WhereCondition;
import io.github.fisher2911.kingdoms.data.sql.dialect.SQLDialect;
import io.github.fisher2911.kingdoms.data.sql.dialect.SystemDialect;
import io.github.fisher2911.kingdoms.data.sql.field.ForeignKeyAction;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLForeignField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLIdField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLKeyType;
import io.github.fisher2911.kingdoms.data.sql.statement.SQLJoinType;
import io.github.fisher2911.kingdoms.data.sql.statement.SQLQuery;
import io.github.fisher2911.kingdoms.data.sql.statement.SQLStatement;
import io.github.fisher2911.kingdoms.data.sql.table.SQLTable;
import io.github.fisher2911.kingdoms.economy.Bank;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomImpl;
import io.github.fisher2911.kingdoms.kingdom.location.KingdomLocations;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.user.BukkitUser;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.Pair;
import io.github.fisher2911.kingdoms.world.Position;
import io.github.fisher2911.kingdoms.world.WorldPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class DataManager {

    // --------------- Kingdom Table ---------------
    private static final String KINGDOM_TABLE_NAME = "kingdoms";
    private static final SQLField KINGDOM_ID_COLUMN = new SQLIdField(KINGDOM_TABLE_NAME, "id", SQLType.INTEGER, SQLKeyType.PRIMARY_KEY, true);
    private static final SQLField KINGDOM_NAME_COLUMN = new SQLField(KINGDOM_TABLE_NAME, "name", SQLType.varchar(30));
    private static final SQLField KINGDOM_DESCRIPTION_COLUMN = new SQLField(KINGDOM_TABLE_NAME, "description", SQLType.varchar(255));
    private static final SQLTable KINGDOM_TABLE = SQLTable.builder(KINGDOM_TABLE_NAME)
            .addFields(KINGDOM_ID_COLUMN, KINGDOM_NAME_COLUMN, KINGDOM_DESCRIPTION_COLUMN)
            .build();


    // --------------- Members Table ---------------
    private static final String MEMBER_TABLE_NAME = "kingdom_members";
    private static final SQLField MEMBER_UUID_COLUMN = new SQLField(MEMBER_TABLE_NAME, "uuid", SQLType.UUID, SQLKeyType.PRIMARY_KEY);
    private static final SQLField MEMBER_KINGDOM_ID_COLUMN = new SQLForeignField(
            MEMBER_TABLE_NAME,
            "kingdom_id",
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLField MEMBER_ROLE_ID_COLUMN = new SQLField(MEMBER_TABLE_NAME, "role_id", SQLType.varchar());
    private static final SQLTable MEMBER_TABLE = SQLTable.builder(MEMBER_TABLE_NAME)
            .addFields(MEMBER_UUID_COLUMN, MEMBER_KINGDOM_ID_COLUMN, MEMBER_ROLE_ID_COLUMN)
            .build();

    // --------------- Permissions Table ---------------
    private static final String PERMISSIONS_TABLE_NAME = "kingdom_permissions";
    private static final SQLField PERMISSIONS_ROLE_ID_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "role_id", SQLType.varchar(), SQLKeyType.UNIQUE);
    private static final SQLField PERMISSIONS_ID_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "id", SQLType.varchar(), SQLKeyType.UNIQUE);
    private static final SQLField PERMISSIONS_VALUE_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "value", SQLType.BOOLEAN);
    private static final SQLField PERMISSIONS_KINGDOM_ID_COLUMN = new SQLForeignField(
            PERMISSIONS_TABLE_NAME,
            "kingdom_id",
            true,
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable PERMISSIONS_TABLE = SQLTable.builder(PERMISSIONS_TABLE_NAME)
            .addFields(PERMISSIONS_ROLE_ID_COLUMN, PERMISSIONS_ID_COLUMN, PERMISSIONS_VALUE_COLUMN, PERMISSIONS_KINGDOM_ID_COLUMN)
            .build();

    // --------------- Chunk Permissions Table ---------------
    private static final String CHUNK_PERMISSIONS_TABLE_NAME = "kingdom_chunk_permissions";
    private static final SQLField CHUNK_PERMISSIONS_ROLE_ID_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "role_id", SQLType.varchar());
    private static final SQLField CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "id", SQLType.varchar(), SQLKeyType.UNIQUE);
    private static final SQLField CHUNK_PERMISSIONS_VALUE_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "value", SQLType.BOOLEAN);
    private static final SQLField CHUNK_PERMISSIONS_ID_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "chunk_id", SQLType.LONG);
    private static final SQLField CHUNK_PERMISSIONS_WORLD_UUID_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "world_uuid", SQLType.UUID);
    private static final SQLField CHUNK_PERMISSIONS_KINGDOM_ID_COLUMN = new SQLForeignField(
            PERMISSIONS_TABLE_NAME,
            "kingdom_id",
            true,
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable CHUNK_PERMISSIONS_TABLE = SQLTable.builder(CHUNK_PERMISSIONS_TABLE_NAME)
            .addFields(
                    CHUNK_PERMISSIONS_ROLE_ID_COLUMN,
                    CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN,
                    CHUNK_PERMISSIONS_VALUE_COLUMN,
                    CHUNK_PERMISSIONS_ID_COLUMN,
                    CHUNK_PERMISSIONS_WORLD_UUID_COLUMN,
                    CHUNK_PERMISSIONS_KINGDOM_ID_COLUMN
            )
            .build();

    // --------------- Upgrade Levels Table ---------------
    private static final String UPGRADE_LEVELS_TABLE_NAME = "kingdom_upgrade_levels";
    private static final SQLField UPGRADE_LEVELS_ID_COLUMN = new SQLField(UPGRADE_LEVELS_TABLE_NAME, "id", SQLType.varchar(32), SQLKeyType.PRIMARY_KEY);
    private static final SQLField UPGRADE_LEVELS_LEVEL_COLUMN = new SQLField(UPGRADE_LEVELS_TABLE_NAME, "level", SQLType.INTEGER);
    private static final SQLField UPGRADE_LEVELS_KINGDOM_ID_COLUMN = new SQLForeignField(
            UPGRADE_LEVELS_TABLE_NAME,
            "kingdom_id",
            true,
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable UPGRADE_LEVELS_TABLE = SQLTable.builder(UPGRADE_LEVELS_TABLE_NAME)
            .addFields(UPGRADE_LEVELS_ID_COLUMN, UPGRADE_LEVELS_LEVEL_COLUMN, UPGRADE_LEVELS_KINGDOM_ID_COLUMN)
            .build();

    // --------------- Relations Table ---------------
    private static final String RELATIONS_TABLE_NAME = "kingdom_relations";
    private static final SQLField RELATIONS_OTHER_KINGDOM_ID_COLUMN = new SQLField(RELATIONS_TABLE_NAME, "other_kingdom_id", SQLType.INTEGER, SQLKeyType.PRIMARY_KEY);
    private static final SQLField RELATIONS_ID_COLUMN = new SQLField(RELATIONS_TABLE_NAME, "relation_type", SQLType.varchar(32));
    private static final SQLField RELATIONS_KINGDOM_ID_COLUMN = new SQLForeignField(
            RELATIONS_TABLE_NAME,
            "kingdom_id",
            true,
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable RELATIONS_TABLE = SQLTable.builder(RELATIONS_TABLE_NAME)
            .addFields(RELATIONS_OTHER_KINGDOM_ID_COLUMN, RELATIONS_ID_COLUMN, RELATIONS_KINGDOM_ID_COLUMN)
            .build();

    // --------------- Bank Table ---------------
    private static final String BANK_TABLE_NAME = "kingdom_bank";
    private static final SQLField BANK_MONEY_COLUMN = new SQLField(BANK_TABLE_NAME, "bank_money", SQLType.DOUBLE);
    private static final SQLField BANK_KINGDOM_ID_COLUMN = new SQLForeignField(
            BANK_TABLE_NAME,
            "kingdom_id",
            true,
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable BANK_TABLE = SQLTable.builder(BANK_TABLE_NAME)
            .addFields(BANK_MONEY_COLUMN, BANK_KINGDOM_ID_COLUMN)
            .build();

    // --------------- Roles Table ---------------
    private static final String ROLES_TABLE_NAME = "kingdom_roles";
    private static final SQLField ROLES_ID_COLUMN = new SQLField(ROLES_TABLE_NAME, "id", SQLType.varchar(32), SQLKeyType.PRIMARY_KEY);
    private static final SQLField ROLES_NAME_COLUMN = new SQLField(ROLES_TABLE_NAME, "name", SQLType.varchar(32));
    private static final SQLField ROLES_WEIGHT_COLUMN = new SQLField(ROLES_TABLE_NAME, "weight", SQLType.INTEGER);
    private static final SQLField ROLES_KINGDOM_ID_COLUMN = new SQLForeignField(
            ROLES_TABLE_NAME,
            "kingdom_id",
            true,
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable ROLES_TABLE = SQLTable.builder(ROLES_TABLE_NAME)
            .addFields(ROLES_ID_COLUMN, ROLES_NAME_COLUMN, ROLES_WEIGHT_COLUMN, ROLES_KINGDOM_ID_COLUMN)
            .build();

    // --------------- Locations Table ---------------
    private static final String LOCATIONS_TABLE_NAME = "kingdom_locations";
    private static final SQLField LOCATIONS_ID_COLUMN = new SQLField(LOCATIONS_TABLE_NAME, "id", SQLType.varchar(30), SQLKeyType.PRIMARY_KEY);
    //    private static final SQLField LOCATIONS_NAME_COLUMN = new SQLField(LOCATIONS_TABLE_NAME, "name", SQLType.varchar(32));
    private static final SQLField LOCATIONS_WORLD_UUID_COLUMN = new SQLField(LOCATIONS_TABLE_NAME, "world_uuid", SQLType.UUID);
    private static final SQLField LOCATIONS_X_COLUMN = new SQLField(LOCATIONS_TABLE_NAME, "x", SQLType.DOUBLE);
    private static final SQLField LOCATIONS_Y_COLUMN = new SQLField(LOCATIONS_TABLE_NAME, "y", SQLType.DOUBLE);
    private static final SQLField LOCATIONS_Z_COLUMN = new SQLField(LOCATIONS_TABLE_NAME, "z", SQLType.DOUBLE);
    private static final SQLField LOCATIONS_YAW_COLUMN = new SQLField(LOCATIONS_TABLE_NAME, "yaw", SQLType.DOUBLE);
    private static final SQLField LOCATIONS_PITCH_COLUMN = new SQLField(LOCATIONS_TABLE_NAME, "pitch", SQLType.DOUBLE);
    private static final SQLField LOCATIONS_KINGDOM_ID_COLUMN = new SQLForeignField(
            LOCATIONS_TABLE_NAME,
            "kingdom_id",
            true,
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable LOCATIONS_TABLE = SQLTable.builder(LOCATIONS_TABLE_NAME)
            .addFields(LOCATIONS_ID_COLUMN, /*LOCATIONS_NAME_COLUMN, */LOCATIONS_WORLD_UUID_COLUMN, LOCATIONS_X_COLUMN, LOCATIONS_Y_COLUMN, LOCATIONS_Z_COLUMN, LOCATIONS_YAW_COLUMN, LOCATIONS_PITCH_COLUMN, LOCATIONS_KINGDOM_ID_COLUMN)
            .build();


    // --------------- User Table ---------------
    private static final String USER_TABLE_NAME = "kingdom_user";
    private static final SQLField USER_UUID_COLUMN = new SQLField(USER_TABLE_NAME, "uuid", SQLType.UUID, SQLKeyType.PRIMARY_KEY);
    private static final SQLField USER_NAME_COLUMN = new SQLField(USER_TABLE_NAME, "name", SQLType.varchar(16));
    private static final SQLField USER_CHAT_CHANNEL_COLUMN = new SQLField(USER_TABLE_NAME, "chat_channel", SQLType.varchar(32));
    private static final SQLField USER_KINGDOM_ID_COLUMN = new SQLForeignField(
            USER_TABLE_NAME,
            "kingdom_id",
            true,
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable USER_TABLE = SQLTable.builder(USER_TABLE_NAME)
            .addFields(USER_UUID_COLUMN, USER_NAME_COLUMN, USER_CHAT_CHANNEL_COLUMN, USER_KINGDOM_ID_COLUMN)
            .build();

    private final Kingdoms plugin;
    private final RoleManager roleManager;
    private final Path databasePath;
    private final Supplier<Connection> dataSource;

    public DataManager(Kingdoms plugin) {
        this.plugin = plugin;
        this.roleManager = this.plugin.getRoleManager();
        this.databasePath = this.plugin.getDataFolder().toPath().resolve("database").resolve("kingdoms.db");
        this.dataSource = this.init();
    }

    private Connection connection;

    private Supplier<Connection> init() {
        final File folder = this.databasePath.getParent().toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (SystemDialect.getDialect() == SQLDialect.SQLITE) {
            return () -> {
                try {
                    if (this.connection != null && !this.connection.isClosed()) return this.connection;
                    this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.databasePath);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return this.connection;
            };
        }
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + this.databasePath);
        config.setConnectionTimeout(5000);
        final HikariDataSource dataSource = new HikariDataSource(config);
        return () -> {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                throw new IllegalStateException("Could not get connection", e);
            }
        };
    }

    public void load() {
        this.init();
        this.createTables();
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.get();
    }

    private void createTables() {
        try (final Connection connection = this.getConnection()) {
            KINGDOM_TABLE.create(connection);
            MEMBER_TABLE.create(connection);
            PERMISSIONS_TABLE.create(connection);
            CHUNK_PERMISSIONS_TABLE.create(connection);
//            CLAIMS_TABLE.create(connection);
            UPGRADE_LEVELS_TABLE.create(connection);
            RELATIONS_TABLE.create(connection);
            BANK_TABLE.create(connection);
            ROLES_TABLE.create(connection);
            LOCATIONS_TABLE.create(connection);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public Kingdom newKingdom(User creator, String name) {
        try {
            final int id = this.createKingdom(this.getConnection(), name, this.plugin.getKingdomSettings().getDefaultKingdomDescription());
            final Kingdom kingdom = new KingdomImpl(
                    this.plugin,
                    id,
                    name,
                    this.plugin.getKingdomSettings().getDefaultKingdomDescription(),
                    new HashMap<>(),
                    new HashMap<>(),
                    this.roleManager.getDefaultRolePermissions(),
                    this.roleManager.getDefaultRolePermissions(),
                    new HashSet<>(),
                    this.plugin.getUpgradeManager().getUpgradeHolder(),
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    Bank.createKingdomBank(0),
                    this.roleManager.createKingdomRoles(),
                    new KingdomLocations(new HashMap<>())
            );
            for (var entry : this.plugin.getRelationManager().createRelations(kingdom).entrySet()) {
                kingdom.setRelation(entry.getKey(), entry.getValue());
            }
            kingdom.addMember(creator);
            kingdom.setRole(creator, this.roleManager.getLeaderRole(kingdom));
            this.save(kingdom);
            return kingdom;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int createKingdom(Connection connection, String name, String desc) throws SQLException {
        final SQLStatement statement = SQLStatement.insert(KINGDOM_TABLE_NAME).
                add(KINGDOM_NAME_COLUMN).
                add(KINGDOM_DESCRIPTION_COLUMN).
                build();
        final List<Object> values = List.of(name, desc);
        final Integer id = statement.insert(connection, List.of(() -> values), 1, SQLStatement.INTEGER_ID_FINDER);
        Bukkit.broadcastMessage("Found id: " + id);
        return id;
    }

    private User createUser(Connection connection, Player player) throws SQLException {
        final SQLStatement statement = SQLStatement.insert(USER_TABLE_NAME).
                add(USER_UUID_COLUMN).
                add(USER_NAME_COLUMN).
                add(USER_CHAT_CHANNEL_COLUMN).
                add(USER_KINGDOM_ID_COLUMN).
                build();
        final List<Object> values = List.of(this.uuidToBytes(player.getUniqueId()), player.getName(), ChatChannel.GLOBAL.toString(), -1);
        statement.insert(connection, List.of(() -> values), 1);
        return new BukkitUser(this.plugin, player, -1, ChatChannel.GLOBAL);
    }

    public void save(Kingdom kingdom) {
        try (final Connection connection = this.getConnection()) {
            connection.setAutoCommit(false);
            this.saveKingdom(connection, kingdom);
            this.saveMembers(connection, kingdom);
            this.savePermissions(connection, kingdom);
            this.saveChunkPermissions(connection, kingdom);
//            this.saveClaims(connection, kingdom);
            this.saveUpgradeLevels(connection, kingdom);
            this.saveRelations(connection, kingdom);
            this.saveBank(connection, kingdom);
            this.saveRoles(connection, kingdom);
            this.saveLocations(connection, kingdom);
            connection.commit();
            connection.setAutoCommit(true);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveKingdom(Connection connection, Kingdom kingdom) throws SQLException {
        final SQLStatement statement = SQLStatement.insert(KINGDOM_TABLE_NAME).
                add(KINGDOM_ID_COLUMN).
                add(KINGDOM_NAME_COLUMN).
                add(KINGDOM_DESCRIPTION_COLUMN).
                build();
        final List<Object> values = List.of(kingdom.getId(), kingdom.getName(), kingdom.getDescription());
        statement.insert(connection, List.of(() -> values), 1);
    }

    private void saveMembers(Connection connection, Kingdom kingdom) throws SQLException {
        final SQLStatement statement = SQLStatement.insert(MEMBER_TABLE_NAME)
                .add(MEMBER_KINGDOM_ID_COLUMN)
                .add(MEMBER_UUID_COLUMN)
                .add(MEMBER_ROLE_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        for (var entry : kingdom.getUserRoles().entrySet()) {
            final List<Object> values = new ArrayList<>();
            final UUID uuid = entry.getKey();
            final Role role = entry.getValue();
            values.add(kingdom.getId());
            values.add(uuidToBytes(uuid));
            values.add(role.id());
            suppliers.add(() -> values);
        }
        statement.insert(connection, suppliers, kingdom.getMembers().size());
    }

    private void savePermissions(Connection connection, Kingdom kingdom) {
        final PermissionContainer permissions = kingdom.getPermissions();
        final SQLStatement statement = SQLStatement.insert(PERMISSIONS_TABLE_NAME)
                .add(PERMISSIONS_KINGDOM_ID_COLUMN)
                .add(PERMISSIONS_ROLE_ID_COLUMN)
                .add(PERMISSIONS_ID_COLUMN)
                .add(PERMISSIONS_VALUE_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize;
        for (var rolePermissionsEntry : permissions.getPermissions().entrySet()) {
            final String role = rolePermissionsEntry.getKey();
            batchSize = rolePermissionsEntry.getValue().size();
            for (var entry : rolePermissionsEntry.getValue().entrySet()) {
                final List<Object> objects = new ArrayList<>();
                final KPermission permission = entry.getKey();
                final boolean value = entry.getValue();
                if (!value) continue;
                objects.add(kingdom.getId());
                objects.add(role);
                objects.add(permission.getId());
                objects.add(value);
                suppliers.add(() -> objects);
            }
            try {
                statement.insert(connection, suppliers, batchSize);
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveChunkPermissions(Connection connection, Kingdom kingdom) {
        final SQLStatement statement = SQLStatement.insert(CHUNK_PERMISSIONS_TABLE_NAME)
                .add(CHUNK_PERMISSIONS_ROLE_ID_COLUMN)
                .add(CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN)
                .add(CHUNK_PERMISSIONS_VALUE_COLUMN)
                .add(CHUNK_PERMISSIONS_ID_COLUMN)
                .add(CHUNK_PERMISSIONS_WORLD_UUID_COLUMN)
                .add(CHUNK_PERMISSIONS_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize = 1;
        for (ClaimedChunk chunk : kingdom.getClaimedChunks()) {
            final PermissionContainer permissions = chunk.getPermissions();
            for (var rolePermissionsEntry : permissions.getPermissions().entrySet()) {
                final String role = rolePermissionsEntry.getKey();
                batchSize = rolePermissionsEntry.getValue().size();
                for (var entry : rolePermissionsEntry.getValue().entrySet()) {
                    final List<Object> objects = new ArrayList<>();
                    final KPermission permission = entry.getKey();
                    final boolean value = entry.getValue();
                    if (!value) continue;
                    objects.add(role);
                    objects.add(permission.getId());
                    objects.add(value);
                    objects.add(chunk.getChunk().getChunkKey());
                    objects.add(uuidToBytes(chunk.getChunk().world()));
                    objects.add(kingdom.getId());
                    suppliers.add(() -> objects);
                }
            }
            try {
                statement.insert(connection, suppliers, batchSize);
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveUpgradeLevels(Connection connection, Kingdom kingdom) {
        final SQLStatement statement = SQLStatement.insert(UPGRADE_LEVELS_TABLE_NAME)
                .add(UPGRADE_LEVELS_ID_COLUMN)
                .add(UPGRADE_LEVELS_LEVEL_COLUMN)
                .add(UPGRADE_LEVELS_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize = kingdom.getUpgradeLevels().size();
        for (var entry : kingdom.getUpgradeLevels().entrySet()) {
            final List<Object> objects = new ArrayList<>();
            final String id = entry.getKey();
            final int level = entry.getValue();
            objects.add(id);
            objects.add(level);
            objects.add(kingdom.getId());
            suppliers.add(() -> objects);
        }
        try {
            statement.insert(connection, suppliers, batchSize);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

    }

    public void saveRelations(Connection connection, Kingdom kingdom) {
        final SQLStatement statement = SQLStatement.insert(RELATIONS_TABLE_NAME)
                .add(RELATIONS_OTHER_KINGDOM_ID_COLUMN)
                .add(RELATIONS_ID_COLUMN)
                .add(RELATIONS_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize = kingdom.getRelations().size();
        for (var entry : kingdom.getKingdomRelations().entrySet()) {
            final List<Object> objects = new ArrayList<>();
            final int otherKingdomId = entry.getKey();
            final RelationInfo relation = entry.getValue();
            objects.add(otherKingdomId);
            objects.add(relation.relationType().toString());
            objects.add(kingdom.getId());
            suppliers.add(() -> objects);
        }
        try {
            statement.insert(connection, suppliers, batchSize);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveBank(Connection connection, Kingdom kingdom) {
        final SQLStatement statement = SQLStatement.insert(BANK_TABLE_NAME)
                .add(BANK_MONEY_COLUMN)
                .add(BANK_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize = 1;
        final List<Object> objects = new ArrayList<>();
        objects.add(kingdom.getBank().getBalance());
        objects.add(kingdom.getId());
        try {
            statement.insert(connection, suppliers, batchSize);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveRoles(Connection connection, Kingdom kingdom) {
        final SQLStatement statement = SQLStatement.insert(ROLES_TABLE_NAME)
                .add(ROLES_ID_COLUMN)
                .add(ROLES_NAME_COLUMN)
                .add(ROLES_WEIGHT_COLUMN)
                .add(ROLES_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize = kingdom.getRoles().size();
        for (var entry : kingdom.getRoles().entrySet()) {
            final List<Object> objects = new ArrayList<>();
            final String id = entry.getKey();
            final Role role = entry.getValue();
            objects.add(id);
            objects.add(role.displayName());
            objects.add(role.weight());
            objects.add(kingdom.getId());
            suppliers.add(() -> objects);
        }
        try {
            statement.insert(connection, suppliers, batchSize);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveLocations(Connection connection, Kingdom kingdom) {
        final SQLStatement statement = SQLStatement.insert(LOCATIONS_TABLE_NAME)
                .add(LOCATIONS_ID_COLUMN)
                .add(LOCATIONS_WORLD_UUID_COLUMN)
                .add(LOCATIONS_X_COLUMN)
                .add(LOCATIONS_Y_COLUMN)
                .add(LOCATIONS_Z_COLUMN)
                .add(LOCATIONS_YAW_COLUMN)
                .add(LOCATIONS_PITCH_COLUMN)
                .add(LOCATIONS_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize = kingdom.getLocations().getSavedPositions().size();
        for (var entry : kingdom.getLocations().getSavedPositions().entrySet()) {
            final List<Object> objects = new ArrayList<>();
            final String id = entry.getKey();
            final WorldPosition worldPosition = entry.getValue();
            final Position position = worldPosition.position();
            final UUID worldUUID = worldPosition.world();
            objects.add(id);
            objects.add(uuidToBytes(worldUUID));
            objects.add(position.x());
            objects.add(position.y());
            objects.add(position.z());
            objects.add(position.yaw());
            objects.add(position.pitch());
            objects.add(kingdom.getId());
            suppliers.add(() -> objects);
        }
        try {
            statement.insert(connection, suppliers, batchSize);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(User user) {
        try {
            saveUser(this.getConnection(), user);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(Connection connection, User user) {
        final SQLStatement statement = SQLStatement.insert(USER_TABLE_NAME)
                .add(USER_UUID_COLUMN)
                .add(USER_NAME_COLUMN)
                .add(USER_CHAT_CHANNEL_COLUMN)
                .add(USER_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize = 1;
        final List<Object> objects = new ArrayList<>();
        objects.add(uuidToBytes(user.getId()));
        objects.add(user.getName());
        objects.add(user.getChatChannel().toString());
        objects.add(user.getKingdomId());
        suppliers.add(() -> objects);
        try {
            statement.insert(connection, suppliers, batchSize);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> loadUser(UUID uuid) {
        try {
            return this.loadUser(this.getConnection(), uuid);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<User> loadUserByName(String name) {
        try {
            return this.loadUserByName(this.getConnection(), name);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<User> loadUser(Connection connection, UUID uuid) {
        final SQLQuery<User> statement = SQLQuery.<User>select(USER_TABLE_NAME)
                .select(USER_NAME_COLUMN, USER_CHAT_CHANNEL_COLUMN, USER_KINGDOM_ID_COLUMN)
                .where(WhereCondition.of(USER_UUID_COLUMN, SQLObject.of(uuidToBytes(uuid))))
                .build();
        try {
            final User user = statement.mapTo(connection, results -> {
                if (results.next()) {
                    final String name = results.getString(USER_NAME_COLUMN.getName());
                    final ChatChannel chatChannel = ChatChannel.valueOf(results.getString(USER_CHAT_CHANNEL_COLUMN.getName()));
                    final int kingdomId = results.getInt(USER_KINGDOM_ID_COLUMN.getName());
                    return new BukkitUser(this.plugin, uuid, name, null, kingdomId, chatChannel);
                }
                return null;
            });
            return Optional.ofNullable(user);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> loadUserByName(Connection connection, String name) {
        final SQLQuery<User> statement = SQLQuery.<User>select(USER_TABLE_NAME)
                .select(USER_UUID_COLUMN, USER_CHAT_CHANNEL_COLUMN, USER_KINGDOM_ID_COLUMN)
                .where(WhereCondition.of(USER_NAME_COLUMN, SQLObject.of(name)))
                .build();
        try {
            final User user = statement.mapTo(connection, results -> {
                if (results.next()) {
                    final UUID uuid = this.bytesToUUID(results.getBytes(USER_UUID_COLUMN.getName()));
                    final ChatChannel chatChannel = ChatChannel.valueOf(results.getString(USER_CHAT_CHANNEL_COLUMN.getName()));
                    final int kingdomId = results.getInt(USER_KINGDOM_ID_COLUMN.getName());
                    return new BukkitUser(this.plugin, uuid, name, null, kingdomId, chatChannel);
                }
                return null;
            });
            return Optional.ofNullable(user);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

//    public Optional<Kingdom> loadKingdom(int kingdomId) {
//        final SQLQuery<Kingdom> query = SQLQuery.<Kingdom>select(KINGDOM_TABLE_NAME)
//                .select(KINGDOM_ID_COLUMN, KINGDOM_NAME_COLUMN, KINGDOM_DESCRIPTION_COLUMN)
//                .select(MEMBER_UUID_COLUMN, MEMBER_ROLE_ID_COLUMN)
//                .select(PERMISSIONS_ROLE_ID_COLUMN, PERMISSIONS_ID_COLUMN, PERMISSIONS_VALUE_COLUMN)
//                .select(CHUNK_PERMISSIONS_ROLE_ID_COLUMN, CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN, CHUNK_PERMISSIONS_VALUE_COLUMN, CHUNK_PERMISSIONS_ID_COLUMN, CHUNK_PERMISSIONS_WORLD_UUID_COLUMN)
//                .select(UPGRADE_LEVELS_ID_COLUMN, UPGRADE_LEVELS_LEVEL_COLUMN)
//                .select(RELATIONS_ID_COLUMN, RELATIONS_KINGDOM_ID_COLUMN)
//                .select(BANK_MONEY_COLUMN)
//                .select(ROLES_ID_COLUMN, ROLES_NAME_COLUMN, ROLES_WEIGHT_COLUMN)
//                .select(LOCATIONS_ID_COLUMN, LOCATIONS_WORLD_UUID_COLUMN, LOCATIONS_X_COLUMN, LOCATIONS_Y_COLUMN, LOCATIONS_Z_COLUMN, LOCATIONS_YAW_COLUMN, LOCATIONS_PITCH_COLUMN)
//                .where(new WhereCondition(List.of(Pair.of(KINGDOM_ID_COLUMN, SQLObject.of(kingdomId)))))
//                .join(KINGDOM_ID_COLUMN, MEMBER_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
//                .join(KINGDOM_ID_COLUMN, PERMISSIONS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
//                .join(KINGDOM_ID_COLUMN, CHUNK_PERMISSIONS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
//                .join(KINGDOM_ID_COLUMN, UPGRADE_LEVELS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
//                .join(KINGDOM_ID_COLUMN, RELATIONS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
//                .join(KINGDOM_ID_COLUMN, BANK_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
//                .join(KINGDOM_ID_COLUMN, ROLES_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
//                .join(KINGDOM_ID_COLUMN, LOCATIONS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
//                .build();
//        System.out.println(query.createStatement());
//        return Optional.empty();
//    }

    public Optional<Kingdom> loadKingdom(int kingdomId) {
        final SQLQuery<Kingdom> query = SQLQuery.<Kingdom>select(KINGDOM_TABLE_NAME)
                .select(KINGDOM_ID_COLUMN, KINGDOM_NAME_COLUMN, KINGDOM_DESCRIPTION_COLUMN)
                .select(MEMBER_UUID_COLUMN, MEMBER_ROLE_ID_COLUMN)
                .select(PERMISSIONS_ROLE_ID_COLUMN, PERMISSIONS_ID_COLUMN, PERMISSIONS_VALUE_COLUMN)
                .select(CHUNK_PERMISSIONS_ROLE_ID_COLUMN, CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN, CHUNK_PERMISSIONS_VALUE_COLUMN, CHUNK_PERMISSIONS_ID_COLUMN, CHUNK_PERMISSIONS_WORLD_UUID_COLUMN)
                .select(UPGRADE_LEVELS_ID_COLUMN, UPGRADE_LEVELS_LEVEL_COLUMN)
                .select(RELATIONS_ID_COLUMN, RELATIONS_KINGDOM_ID_COLUMN)
                .select(BANK_MONEY_COLUMN)
                .select(ROLES_ID_COLUMN, ROLES_NAME_COLUMN, ROLES_WEIGHT_COLUMN)
                .select(LOCATIONS_ID_COLUMN, LOCATIONS_WORLD_UUID_COLUMN, LOCATIONS_X_COLUMN, LOCATIONS_Y_COLUMN, LOCATIONS_Z_COLUMN, LOCATIONS_YAW_COLUMN, LOCATIONS_PITCH_COLUMN)
                .where(new WhereCondition(List.of(Pair.of(KINGDOM_ID_COLUMN, SQLObject.of(kingdomId)))))
                .join(KINGDOM_ID_COLUMN, MEMBER_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .join(KINGDOM_ID_COLUMN, PERMISSIONS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .join(KINGDOM_ID_COLUMN, CHUNK_PERMISSIONS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .join(KINGDOM_ID_COLUMN, UPGRADE_LEVELS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .join(KINGDOM_ID_COLUMN, RELATIONS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .join(KINGDOM_ID_COLUMN, BANK_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .join(KINGDOM_ID_COLUMN, ROLES_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .join(KINGDOM_ID_COLUMN, LOCATIONS_KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .build();
        System.out.println(query.createStatement());
        return Optional.empty();
    }

    // todo
    public Optional<Kingdom> loadKingdomByName(String name) {
        return Optional.empty();
    }
//
//    private Set<User> getKingdomUsers(int kingdomId) {
//        final SQLQuery<Kingdom> query = SQLQuery.<Kingdom>select(MEMBER_TABLE_NAME)
//                .select(MEMBER_UUID_COLUMN, MEMBER_ROLE_ID_COLUMN)
//                .where(new WhereCondition(List.of(Pair.of(MEMBER_KINGDOM_ID_COLUMN, SQLObject.of(kingdomId)))))
//                .build();
//        final Set<User> users = new HashSet<>();
//
//    }


//    @Nullable
//    public Optional<Kingdom> getKingdomByName(Connection connection, String name) {
//        final SQLQuery<Kingdom> statement = SQLQuery.<Kingdom>
//                        select(KINGDOM_TABLE_NAME)
//                .select(KINGDOM_ID_COLUMN, )
//                .where(new WhereCondition(List.of(Pair.of(KINGDOM_NAME_COLUMN, SQLObject.of(name)))))
//                .build();
//        try {
//            statement.mapTo(connection, this::mapKingdom);
//            final List<Kingdom> kingdoms = statement.query(connection, this::getKingdomFromResultSet);
//            if (kingdoms.size() == 0) {
//                return Optional.empty();
//            }
//            return Optional.of(kingdoms.get(0));
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        }
//        return Optional.empty();
//    }

    private byte[] uuidToBytes(UUID uuid) {
        return ByteBuffer.wrap(new byte[16])
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits()).array();
    }

    private UUID bytesToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

}
