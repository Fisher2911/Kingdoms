/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.kingdoms.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.data.sql.SQLType;
import io.github.fisher2911.kingdoms.data.sql.condition.WhereCondition;
import io.github.fisher2911.kingdoms.data.sql.dialect.SQLDialect;
import io.github.fisher2911.kingdoms.data.sql.dialect.SystemDialect;
import io.github.fisher2911.kingdoms.data.sql.field.ForeignKeyAction;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLForeignField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLIdField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLKeyType;
import io.github.fisher2911.kingdoms.data.sql.statement.DeleteStatement;
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
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.BukkitUser;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.MapOfMaps;
import io.github.fisher2911.kingdoms.util.Pair;
import io.github.fisher2911.kingdoms.world.KChunk;
import io.github.fisher2911.kingdoms.world.Position;
import io.github.fisher2911.kingdoms.world.WorldPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class DataManager {

    // --------------- Kingdom Table ---------------
    private static final String KINGDOM_TABLE_NAME = "kingdoms";
    private static final SQLField KINGDOM_ID_COLUMN = new SQLIdField(KINGDOM_TABLE_NAME, "id", SQLType.INTEGER, SQLKeyType.PRIMARY_KEY, true);
    private static final SQLField KINGDOM_NAME_COLUMN = new SQLField(KINGDOM_TABLE_NAME, "name", SQLType.varchar());
    private static final SQLField KINGDOM_DESCRIPTION_COLUMN = new SQLField(KINGDOM_TABLE_NAME, "description", SQLType.varchar());
    private static final SQLField KINGDOM_CREATED_DATE_COLUMN = new SQLField(KINGDOM_TABLE_NAME, "created_date", SQLType.DATE_TIME);
    private static final SQLTable KINGDOM_TABLE = SQLTable.builder(KINGDOM_TABLE_NAME)
            .addFields(KINGDOM_ID_COLUMN, KINGDOM_NAME_COLUMN, KINGDOM_DESCRIPTION_COLUMN, KINGDOM_CREATED_DATE_COLUMN)
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
    private static final SQLField PERMISSIONS_ID_COLUMN = new SQLField(PERMISSIONS_TABLE_NAME, "id", SQLType.INTEGER, SQLKeyType.UNIQUE);
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

    // --------------- Chunk Table ---------------
    private static final String CHUNK_TABLE_NAME = "kingdom_chunks";
    private static final SQLField CHUNK_KEY_COLUMN = new SQLField(CHUNK_TABLE_NAME, "key", SQLType.LONG, SQLKeyType.PRIMARY_KEY);
    private static final SQLField CHUNK_WORLD_UUID_COLUMN = new SQLField(CHUNK_TABLE_NAME, "world_uuid", SQLType.UUID);
    private static final SQLField CHUNK_X_COLUMN = new SQLField(CHUNK_TABLE_NAME, "x", SQLType.INTEGER);
    private static final SQLField CHUNK_Z_COLUMN = new SQLField(CHUNK_TABLE_NAME, "z", SQLType.INTEGER);
    private static final SQLField CHUNK_KINGDOM_ID_COLUMN = new SQLForeignField(
            CHUNK_TABLE_NAME,
            "kingdom_id",
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_CASCADE
    );
    private static final SQLTable CHUNK_TABLE = SQLTable.builder(CHUNK_TABLE_NAME)
            .addFields(CHUNK_KEY_COLUMN, CHUNK_WORLD_UUID_COLUMN, CHUNK_X_COLUMN, CHUNK_Z_COLUMN, CHUNK_KINGDOM_ID_COLUMN)
            .build();


    // --------------- Chunk Permissions Table ---------------
    private static final String CHUNK_PERMISSIONS_TABLE_NAME = "kingdom_chunk_permissions";
    private static final SQLField CHUNK_PERMISSIONS_ROLE_ID_COLUMN = new SQLField(CHUNK_PERMISSIONS_TABLE_NAME, "role_id", SQLType.varchar());
    private static final SQLField CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN = new SQLField(CHUNK_PERMISSIONS_TABLE_NAME, "id", SQLType.INTEGER, SQLKeyType.UNIQUE);
    private static final SQLField CHUNK_PERMISSIONS_VALUE_COLUMN = new SQLField(CHUNK_PERMISSIONS_TABLE_NAME, "value", SQLType.BOOLEAN);
    private static final SQLField CHUNK_PERMISSIONS_ID_COLUMN = new SQLField(CHUNK_PERMISSIONS_TABLE_NAME, "chunk_id", SQLType.LONG);
    private static final SQLField CHUNK_PERMISSIONS_WORLD_UUID_COLUMN = new SQLField(CHUNK_PERMISSIONS_TABLE_NAME, "world_uuid", SQLType.UUID);
    private static final SQLField CHUNK_PERMISSIONS_KINGDOM_ID_COLUMN = new SQLForeignField(
            CHUNK_PERMISSIONS_TABLE_NAME,
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
    private static final SQLField ROLES_ID_COLUMN = new SQLField(ROLES_TABLE_NAME, "id", SQLType.varchar(32), SQLKeyType.UNIQUE);
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
            .addFields(LOCATIONS_ID_COLUMN, LOCATIONS_WORLD_UUID_COLUMN, LOCATIONS_X_COLUMN, LOCATIONS_Y_COLUMN, LOCATIONS_Z_COLUMN, LOCATIONS_YAW_COLUMN, LOCATIONS_PITCH_COLUMN, LOCATIONS_KINGDOM_ID_COLUMN)
            .build();


    // --------------- User Table ---------------
    private static final String USER_TABLE_NAME = "users";
    private static final SQLField USER_UUID_COLUMN = new SQLField(USER_TABLE_NAME, "uuid", SQLType.UUID, SQLKeyType.PRIMARY_KEY);
    private static final SQLField USER_NAME_COLUMN = new SQLField(USER_TABLE_NAME, "name", SQLType.varchar(16));
    private static final SQLField USER_CHAT_CHANNEL_COLUMN = new SQLField(USER_TABLE_NAME, "chat_channel", SQLType.varchar(32));
    private static final SQLField USER_KINGDOM_ID_COLUMN = new SQLForeignField(
            USER_TABLE_NAME,
            "kingdom_id",
            SQLType.INTEGER,
            KINGDOM_TABLE_NAME,
            List.of(KINGDOM_ID_COLUMN),
            ForeignKeyAction.ON_DELETE_SET_DEFAULT
    );
    private static final SQLTable USER_TABLE = SQLTable.builder(USER_TABLE_NAME)
            .addFields(USER_UUID_COLUMN, USER_NAME_COLUMN, USER_CHAT_CHANNEL_COLUMN, USER_KINGDOM_ID_COLUMN)
            .build();

    private final Kingdoms plugin;
    private final Path databasePath;
    private final Supplier<Connection> dataSource;

    public DataManager(Kingdoms plugin) {
        this.plugin = plugin;
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
            CHUNK_TABLE.create(connection);
            CHUNK_PERMISSIONS_TABLE.create(connection);
            UPGRADE_LEVELS_TABLE.create(connection);
            RELATIONS_TABLE.create(connection);
            BANK_TABLE.create(connection);
            ROLES_TABLE.create(connection);
            LOCATIONS_TABLE.create(connection);
            USER_TABLE.create(connection);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public Kingdom newKingdom(User creator, String name) {
        final RoleManager roleManager = this.plugin.getRoleManager();
        try {
            final Instant now = Instant.now();
            final int id = this.createKingdom(
                    this.getConnection(),
                    name,
                    this.plugin.getKingdomSettings().getDefaultKingdomDescription(),
                    now
            );
            final Kingdom kingdom = new KingdomImpl(
                    this.plugin,
                    id,
                    name,
                    this.plugin.getKingdomSettings().getDefaultKingdomDescription(),
                    new HashMap<>(),
                    new HashMap<>(),
                    roleManager.getDefaultRolePermissions(),
                    new HashSet<>(),
                    this.plugin.getUpgradeManager().getUpgradeHolder(),
                    new HashMap<>(),
                    new HashMap<>(),
                    Bank.createKingdomBank(0),
                    roleManager.createKingdomRoles(),
                    new KingdomLocations(new HashMap<>()),
                    now
            );
            kingdom.addMember(creator);
            kingdom.setRole(creator, roleManager.getLeaderRole(kingdom));
            return kingdom;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int createKingdom(Connection connection, String name, String desc, Instant createdAt) throws SQLException {
        final SQLStatement statement = SQLStatement.insert(KINGDOM_TABLE_NAME).
                add(KINGDOM_NAME_COLUMN).
                add(KINGDOM_DESCRIPTION_COLUMN).
                add(KINGDOM_CREATED_DATE_COLUMN).
                build();
        final List<Object> values = List.of(name, desc, new Timestamp(createdAt.toEpochMilli()));
        final Integer id = statement.insert(connection, List.of(() -> values), 1, SQLStatement.INTEGER_ID_FINDER);
        if (id == null) {
            throw new IllegalStateException("Could not create kingdom");
        }
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

    public void saveKingdom(Kingdom kingdom) {
        try (final Connection connection = this.getConnection()) {
            connection.setAutoCommit(false);
            this.saveKingdom(connection, kingdom);
            this.saveMembers(connection, kingdom);
            this.savePermissions(connection, kingdom);
            this.saveClaimedChunks(connection, kingdom);
            this.saveUpgradeLevels(connection, kingdom);
            this.saveBank(connection, kingdom);
            this.saveRoles(connection, kingdom);
            this.saveKingdomRelations(connection, kingdom);
            this.saveLocations(connection, kingdom);
            connection.commit();
            kingdom.setDirty(false);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveKingdom(Connection connection, Kingdom kingdom) throws SQLException {
        final SQLStatement statement = SQLStatement.insert(KINGDOM_TABLE_NAME)
                .add(KINGDOM_ID_COLUMN)
                .add(KINGDOM_NAME_COLUMN)
                .add(KINGDOM_DESCRIPTION_COLUMN)
                .add(KINGDOM_CREATED_DATE_COLUMN)
                .build();
        final List<Object> values = List.of(kingdom.getId(), kingdom.getName(), kingdom.getDescription(), Timestamp.from(kingdom.getCreatedAt()));
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

    public Map<UUID, String> loadMembers(Connection connection, int kingdomId) throws SQLException {
        final SQLQuery<Map<UUID, String>> query = SQLQuery.<Map<UUID, String>>select(MEMBER_TABLE_NAME)
                .select(MEMBER_UUID_COLUMN, MEMBER_ROLE_ID_COLUMN)
                .where(WhereCondition.of(MEMBER_KINGDOM_ID_COLUMN, () -> kingdomId))
                .build();
        return query.mapTo(connection, resultSet -> {
            final Map<UUID, String> members = new HashMap<>();
            while (resultSet.next()) {
                final UUID uuid = this.bytesToUUID(resultSet.getBytes(MEMBER_UUID_COLUMN.getName()));
                final String roleId = resultSet.getString(MEMBER_ROLE_ID_COLUMN.getName());
                members.put(uuid, roleId);
            }
            return members;
        });
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
                objects.add(kingdom.getId());
                objects.add(role);
                objects.add(permission.getIntId());
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

    public PermissionContainer loadPermissions(Connection connection, int kingdomId, Map<String, Role> roles) throws SQLException {
        final SQLQuery<PermissionContainer> query = SQLQuery.<PermissionContainer>select(PERMISSIONS_TABLE_NAME)
                .select(PERMISSIONS_ROLE_ID_COLUMN, PERMISSIONS_ID_COLUMN, PERMISSIONS_VALUE_COLUMN)
                .where(WhereCondition.of(PERMISSIONS_KINGDOM_ID_COLUMN, () -> kingdomId))
                .build();
        return query.mapTo(connection, resultSet -> {
            final PermissionContainer container = new PermissionContainer(MapOfMaps.newHashMap());
            while (resultSet.next()) {
                final String roleId = resultSet.getString(PERMISSIONS_ROLE_ID_COLUMN.getName());
                final int permissionId = resultSet.getInt(PERMISSIONS_ID_COLUMN.getName());
                final KPermission permission = KPermission.get(permissionId);
                final boolean value = resultSet.getBoolean(PERMISSIONS_VALUE_COLUMN.getName());
                final Role role = roles.get(roleId);
                if (role == null) continue;
                container.setPermission(role, permission, value);
            }
            return container;
        });
    }

    public void saveClaimedChunks(Collection<ClaimedChunk> chunks) {
        try {
            for (ClaimedChunk chunk : chunks) {
                if (!chunk.isDirty()) continue;
                this.saveClaimedChunk(this.getConnection(), chunk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveClaimedChunk(ClaimedChunk chunk) {
        try {
            this.saveClaimedChunk(this.getConnection(), chunk);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveClaimedChunk(Connection connection, ClaimedChunk chunk) throws SQLException {
        if (chunk.getKingdomId() == Kingdom.WILDERNESS_ID) return;
        final SQLStatement statement = SQLStatement.insert(CHUNK_TABLE_NAME)
                .add(CHUNK_KEY_COLUMN)
                .add(CHUNK_KINGDOM_ID_COLUMN)
                .add(CHUNK_WORLD_UUID_COLUMN)
                .add(CHUNK_X_COLUMN)
                .add(CHUNK_Z_COLUMN)
                .build();
        final KChunk kChunk = chunk.getChunk();
        final List<Object> values = List.of(chunk.getChunk().getChunkKey(), chunk.getKingdomId(), uuidToBytes(chunk.getWorld()), kChunk.x(), kChunk.z());
        statement.insert(connection, List.of(() -> values), 1);
        this.saveChunkPermissions(connection, chunk);
    }

    private void saveChunkPermissions(Connection connection, ClaimedChunk chunk) {
        if (chunk.getKingdomId() == Kingdom.WILDERNESS_ID) return;
        final SQLStatement statement = SQLStatement.insert(CHUNK_PERMISSIONS_TABLE_NAME)
                .add(CHUNK_PERMISSIONS_ROLE_ID_COLUMN)
                .add(CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN)
                .add(CHUNK_PERMISSIONS_VALUE_COLUMN)
                .add(CHUNK_PERMISSIONS_ID_COLUMN)
                .add(CHUNK_PERMISSIONS_WORLD_UUID_COLUMN)
                .add(CHUNK_PERMISSIONS_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize;
        final PermissionContainer permissions = chunk.getPermissions();
        for (var rolePermissionsEntry : permissions.getPermissions().entrySet()) {
            final String role = rolePermissionsEntry.getKey();
            batchSize = rolePermissionsEntry.getValue().size();
            for (var entry : rolePermissionsEntry.getValue().entrySet()) {
                final List<Object> objects = new ArrayList<>();
                final KPermission permission = entry.getKey();
                final boolean value = entry.getValue();
                objects.add(role);
                objects.add(permission.getIntId());
                objects.add(value);
                objects.add(chunk.getChunk().getChunkKey());
                objects.add(uuidToBytes(chunk.getChunk().world()));
                objects.add(chunk.getKingdomId());
                suppliers.add(() -> objects);
            }
            try {
                statement.insert(connection, suppliers, batchSize);
                chunk.setDirty(false);
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private PermissionContainer loadChunkPermissions(Connection connection, long chunkKey, int kingdomId) throws SQLException {
        final SQLQuery<PermissionContainer> query = SQLQuery.<PermissionContainer>select(CHUNK_PERMISSIONS_TABLE_NAME)
                .select(CHUNK_PERMISSIONS_ROLE_ID_COLUMN, CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN, CHUNK_PERMISSIONS_VALUE_COLUMN)
                .where(new WhereCondition(List.of(
                        Pair.of(CHUNK_PERMISSIONS_ID_COLUMN, () -> chunkKey),
                        Pair.of(CHUNK_PERMISSIONS_KINGDOM_ID_COLUMN, () -> kingdomId)
                )
                ))
                .build();
        return query.mapTo(connection, results -> {
            final PermissionContainer container = new PermissionContainer(MapOfMaps.newHashMap());
            while (results.next()) {
                final String role = results.getString(CHUNK_PERMISSIONS_ROLE_ID_COLUMN.getName());
                final int permissionId = results.getInt(CHUNK_PERMISSIONS_PERMISSION_ID_COLUMN.getName());
                final boolean value = results.getBoolean(CHUNK_PERMISSIONS_VALUE_COLUMN.getName());
                final KPermission permission = KPermission.get(permissionId);
                if (permission == null) continue;
                container.setPermission(role, permission, value);
            }
            return container;
        });
    }

    private Set<ClaimedChunk> loadClaimedChunks(Connection connection, int kingdomId) throws SQLException {
        final SQLQuery<Set<ClaimedChunk>> query = SQLQuery.<Set<ClaimedChunk>>select(CHUNK_TABLE_NAME)
                .select(CHUNK_KEY_COLUMN, CHUNK_WORLD_UUID_COLUMN, CHUNK_X_COLUMN, CHUNK_Z_COLUMN)
                .where(new WhereCondition(List.of(
                        Pair.of(CHUNK_KINGDOM_ID_COLUMN, () -> kingdomId)
                )))
                .build();

        return query.mapTo(connection, results -> {
            final Set<ClaimedChunk> chunks = new HashSet<>();
            while (results.next()) {
                final long chunkKey = results.getLong(CHUNK_KEY_COLUMN.getName());
                final UUID world = bytesToUUID(results.getBytes(CHUNK_WORLD_UUID_COLUMN.getName()));
                final int x = results.getInt(CHUNK_X_COLUMN.getName());
                final int z = results.getInt(CHUNK_Z_COLUMN.getName());
                final KChunk chunk = new KChunk(world, x, z);
                final PermissionContainer permissions = this.loadChunkPermissions(connection, chunkKey, kingdomId);
                final ClaimedChunk claimedChunk = new ClaimedChunk(this.plugin, kingdomId, chunk, permissions);
                chunks.add(claimedChunk);
            }
            return chunks;
        });
    }

    @Nullable
    public ClaimedChunk loadClaimedChunk(long chunkKey) {
        try {
            return this.loadClaimedChunk(this.getConnection(), chunkKey);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load chunk with key " + chunkKey, e);
        }
    }

    @Nullable
    public ClaimedChunk loadClaimedChunk(Connection connection, long chunkKey) throws SQLException {
        final SQLQuery<ClaimedChunk> statement = SQLQuery.<ClaimedChunk>select(CHUNK_TABLE_NAME)
                .select(CHUNK_WORLD_UUID_COLUMN, CHUNK_X_COLUMN, CHUNK_Z_COLUMN, CHUNK_KINGDOM_ID_COLUMN)
                .where(WhereCondition.of(CHUNK_KEY_COLUMN, () -> chunkKey))
                .build();
        return statement.mapTo(connection, results -> {
            if (!results.next()) return null;
            final int kingdomId = results.getInt(CHUNK_KINGDOM_ID_COLUMN.getName());
            final int x = results.getInt(CHUNK_X_COLUMN.getName());
            final int z = results.getInt(CHUNK_Z_COLUMN.getName());
            final UUID world = bytesToUUID(results.getBytes(CHUNK_WORLD_UUID_COLUMN.getName()));
            final PermissionContainer container = this.loadChunkPermissions(connection, chunkKey, kingdomId);
            return new ClaimedChunk(this.plugin, kingdomId, KChunk.at(world, x, z), container);
        });
    }

    private void saveClaimedChunks(Connection connection, Kingdom kingdom) throws SQLException {
        for (final ClaimedChunk chunk : kingdom.getClaimedChunks()) {
            if (chunk.isDirty()) continue;
            saveClaimedChunk(connection, chunk);
        }
    }

    public void saveUpgradeLevels(Connection connection, Kingdom kingdom) throws SQLException {
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
        statement.insert(connection, suppliers, batchSize);
    }

    public Map<String, Integer> loadUpgradeLevels(Connection connection, int kingdomId) throws SQLException {
        final SQLQuery<Map<String, Integer>> query = SQLQuery.<Map<String, Integer>>select(UPGRADE_LEVELS_TABLE_NAME)
                .select(UPGRADE_LEVELS_ID_COLUMN, UPGRADE_LEVELS_LEVEL_COLUMN)
                .where(new WhereCondition(List.of(
                        Pair.of(UPGRADE_LEVELS_KINGDOM_ID_COLUMN, () -> kingdomId)
                )))
                .build();
        return query.mapTo(connection, results -> {
            final Map<String, Integer> levels = new HashMap<>();
            while (results.next()) {
                final String id = results.getString(UPGRADE_LEVELS_ID_COLUMN.getName());
                final int level = results.getInt(UPGRADE_LEVELS_LEVEL_COLUMN.getName());
                levels.put(id, level);
            }
            return levels;
        });
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
        suppliers.add(() -> objects);
        try {
            statement.insert(connection, suppliers, batchSize);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public Bank<Kingdom> loadBank(Connection connection, int kingdomId) throws SQLException {
        final SQLQuery<Bank<Kingdom>> query = SQLQuery.<Bank<Kingdom>>select(BANK_TABLE_NAME)
                .select(BANK_MONEY_COLUMN)
                .where(new WhereCondition(List.of(
                        Pair.of(BANK_KINGDOM_ID_COLUMN, () -> kingdomId)
                )))
                .build();
        return query.mapTo(connection, results -> {
            if (!results.next()) return null;
            final double money = results.getDouble(BANK_MONEY_COLUMN.getName());
            return Bank.createKingdomBank(money);
        });
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

    public Map<String, Role> loadRoles(Connection connection, int kingdomId) throws SQLException {
        final SQLQuery<Map<String, Role>> query = SQLQuery.<Map<String, Role>>select(ROLES_TABLE_NAME)
                .select(ROLES_ID_COLUMN, ROLES_NAME_COLUMN, ROLES_WEIGHT_COLUMN)
                .where(WhereCondition.of(ROLES_KINGDOM_ID_COLUMN, () -> kingdomId))
                .build();
        return query.mapTo(connection, results -> {
            final Map<String, Role> roles = new HashMap<>();
            while (results.next()) {
                final String id = results.getString(ROLES_ID_COLUMN.getName());
                final String name = results.getString(ROLES_NAME_COLUMN.getName());
                final int weight = results.getInt(ROLES_WEIGHT_COLUMN.getName());
                roles.put(id, new Role(id, name, weight));
            }
            return roles;
        });
    }

    public void saveKingdomRelations(Connection connection, Kingdom kingdom) {
        final SQLStatement statement = SQLStatement.insert(RELATIONS_TABLE_NAME)
                .add(RELATIONS_OTHER_KINGDOM_ID_COLUMN)
                .add(RELATIONS_ID_COLUMN)
                .add(RELATIONS_KINGDOM_ID_COLUMN)
                .build();
        final List<Supplier<List<Object>>> suppliers = new ArrayList<>();
        int batchSize = kingdom.getKingdomRelations().size();
        for (var entry : kingdom.getKingdomRelations().entrySet()) {
            final List<Object> objects = new ArrayList<>();
            final int otherKingdom = entry.getKey();
            final RelationInfo relation = entry.getValue();
            objects.add(otherKingdom);
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

    public Map<Integer, RelationInfo> loadKingdomRelations(Connection connection, int kingomId) throws SQLException {
        final SQLQuery<Map<Integer, RelationInfo>> query = SQLQuery.<Map<Integer, RelationInfo>>select(RELATIONS_TABLE_NAME)
                .select(RELATIONS_OTHER_KINGDOM_ID_COLUMN, RELATIONS_ID_COLUMN)
                .select(KINGDOM_NAME_COLUMN)
                .where(WhereCondition.of(RELATIONS_KINGDOM_ID_COLUMN, () -> kingomId))
                .join(RELATIONS_KINGDOM_ID_COLUMN, KINGDOM_ID_COLUMN, SQLJoinType.LEFT_JOIN)
                .build();
        return query.mapTo(connection, results -> {
            final Map<Integer, RelationInfo> relations = new HashMap<>();
            while (results.next()) {
                final int otherKingdom = results.getInt(RELATIONS_OTHER_KINGDOM_ID_COLUMN.getAliasName());
                final String relationType = results.getString(RELATIONS_ID_COLUMN.getAliasName());
                final String otherKingdomName = results.getString(KINGDOM_NAME_COLUMN.getAliasName());
                relations.put(otherKingdom, new RelationInfo(otherKingdom, otherKingdomName, RelationType.valueOf(relationType)));
            }
            return relations;
        });
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

    public KingdomLocations loadKingdomLocations(Connection connection, int kingdomId) throws SQLException {
        final SQLQuery<KingdomLocations> query = SQLQuery.<KingdomLocations>select(LOCATIONS_TABLE_NAME)
                .select(LOCATIONS_ID_COLUMN, LOCATIONS_WORLD_UUID_COLUMN, LOCATIONS_X_COLUMN, LOCATIONS_Y_COLUMN, LOCATIONS_Z_COLUMN, LOCATIONS_YAW_COLUMN, LOCATIONS_PITCH_COLUMN)
                .where(WhereCondition.of(LOCATIONS_KINGDOM_ID_COLUMN, () -> kingdomId))
                .build();
        return query.mapTo(connection, results -> {
            final KingdomLocations locations = new KingdomLocations(new HashMap<>());
            while (results.next()) {
                final String id = results.getString(LOCATIONS_ID_COLUMN.getName());
                final UUID worldUUID = bytesToUUID(results.getBytes(LOCATIONS_WORLD_UUID_COLUMN.getName()));
                final double x = results.getDouble(LOCATIONS_X_COLUMN.getName());
                final double y = results.getDouble(LOCATIONS_Y_COLUMN.getName());
                final double z = results.getDouble(LOCATIONS_Z_COLUMN.getName());
                final float yaw = results.getFloat(LOCATIONS_YAW_COLUMN.getName());
                final float pitch = results.getFloat(LOCATIONS_PITCH_COLUMN.getName());
                locations.setPosition(id, new WorldPosition(worldUUID, new Position(x, y, z, yaw, pitch)));
            }
            return locations;
        });
    }

    public void saveUser(User user) {
        try {
            saveUser(this.getConnection(), user);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(Connection connection, User user) throws SQLException {
        connection.setAutoCommit(false);
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
            connection.commit();
            user.setDirty(false);
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

    public Optional<User> loadUser(Connection connection, UUID uuid) throws SQLException {
        final SQLQuery<User> statement = SQLQuery.<User>select(USER_TABLE_NAME)
                .select(USER_UUID_COLUMN, USER_NAME_COLUMN, USER_CHAT_CHANNEL_COLUMN, USER_KINGDOM_ID_COLUMN)
                .where(WhereCondition.of(USER_UUID_COLUMN, () -> uuidToBytes(uuid)))
                .build();

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
    }

    public Optional<User> loadUserByName(Connection connection, String name) {
        final SQLQuery<User> statement = SQLQuery.<User>select(USER_TABLE_NAME)
                .select(USER_UUID_COLUMN, USER_CHAT_CHANNEL_COLUMN, USER_KINGDOM_ID_COLUMN)
                .where(WhereCondition.of(USER_NAME_COLUMN, () -> name))
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

    public Optional<Kingdom> loadKingdom(int kingdomId) {
        try {
            return this.loadKingdom(this.getConnection(), kingdomId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<Kingdom> loadKingdom(Connection connection, int kingdomId) throws SQLException {
        if (kingdomId == Kingdom.WILDERNESS_ID) return Optional.empty();
        final SQLQuery<Kingdom> query = SQLQuery.<Kingdom>select(KINGDOM_TABLE_NAME)
                .select(KINGDOM_ID_COLUMN, KINGDOM_NAME_COLUMN, KINGDOM_DESCRIPTION_COLUMN, KINGDOM_CREATED_DATE_COLUMN)
                .where(WhereCondition.of(KINGDOM_ID_COLUMN, () -> kingdomId))
                .build();

        final Set<ClaimedChunk> chunks = this.loadClaimedChunks(connection, kingdomId);
        final Map<String, Integer> upgradeLevels = this.loadUpgradeLevels(connection, kingdomId);

        final Map<String, Role> roles = this.loadRoles(connection, kingdomId);
        final Map<UUID, String> memberRoleIds = this.loadMembers(connection, kingdomId);
        final Map<UUID, Role> memberRoles = new HashMap<>();
        for (final Map.Entry<UUID, String> entry : memberRoleIds.entrySet()) {
            final Role role = roles.get(entry.getValue());
            if (role == null) {
                throw new IllegalStateException("Could not find role with id " + entry.getValue());
            }
            memberRoles.put(entry.getKey(), role);
        }
        final Map<UUID, User> users = new HashMap<>();
        for (UUID uuid : memberRoles.keySet()) {
            final User user = this.plugin.getUserManager().forceGet(uuid);
            if (user != null) {
                users.put(uuid, user);
                continue;
            }
            final Optional<User> optional = this.loadUser(connection, uuid);
            if (optional.isEmpty()) continue;
            final User loadedUser = optional.get();
            this.plugin.getUserManager().addUser(loadedUser);
            users.put(uuid, loadedUser);
        }
        final PermissionContainer permissions = this.loadPermissions(connection, kingdomId, roles);
        final Bank<Kingdom> bank = this.loadBank(connection, kingdomId);
        final Map<Integer, RelationInfo> kingdomRelations = this.loadKingdomRelations(connection, kingdomId);
        final KingdomLocations locations = this.loadKingdomLocations(connection, kingdomId);
        return Optional.ofNullable(query.mapTo(connection, results -> {
            if (!results.next()) return null;
            final int id = results.getInt(KINGDOM_ID_COLUMN.getName());
            final String name = results.getString(KINGDOM_NAME_COLUMN.getName());
            final String description = results.getString(KINGDOM_DESCRIPTION_COLUMN.getName());
            final Timestamp creationTime = results.getTimestamp(KINGDOM_CREATED_DATE_COLUMN.getName());
            TaskChain.create(this.plugin)
                    .runSync(() -> {
                        for (ClaimedChunk chunk : chunks) {
                            this.plugin.getWorldManager().setChunk(chunk);
                        }
                    })
                    .execute();
            return new KingdomImpl(
                    this.plugin,
                    id,
                    name,
                    description,
                    users,
                    memberRoles,
                    permissions,
                    chunks,
                    this.plugin.getUpgradeManager().getUpgradeHolder(),
                    upgradeLevels,
                    kingdomRelations,
                    bank,
                    roles,
                    locations,
                    creationTime.toInstant()
            );
        }));
    }

    public void deleteKingdom(int kingdomId) {
        try {
            this.deleteKingdom(this.getConnection(), kingdomId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteKingdom(Connection connection, int kingdomId) throws SQLException {
        DeleteStatement.builder(KINGDOM_TABLE)
                .where(WhereCondition.of(KINGDOM_ID_COLUMN, () -> kingdomId))
                .build()
                .execute(connection);
    }

    public Optional<Kingdom> loadKingdomByName(String name) {
        try {
            return this.loadKingdomByName(this.getConnection(), name);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<Kingdom> loadKingdomByName(Connection connection, String name) throws SQLException {
        final SQLQuery<Optional<Kingdom>> query = SQLQuery.<Optional<Kingdom>>select(KINGDOM_TABLE_NAME)
                .select(KINGDOM_ID_COLUMN)
                .where(WhereCondition.of(KINGDOM_NAME_COLUMN, () -> name))
                .build();
        return query.mapTo(this.getConnection(), results -> {
            if (!results.next()) return Optional.empty();
            final int kingdomId = results.getInt(KINGDOM_ID_COLUMN.getName());
            return this.loadKingdom(connection, kingdomId);
        });
    }

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
