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

package io.github.fisher2911.kingdoms.config;

import io.github.fisher2911.kingdoms.Kingdoms;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;

public class KingdomsSettings extends Config {

    public KingdomsSettings(Kingdoms plugin) {
        super(plugin, "config.yml");
    }

    private static final String DEFAULT_KINGDOM_DESCRIPTION_PATH = "default-kingdom-description";
    private static final String TELEPORT_DELAY_PATH = "teleport-delay";
    private static final String SAVE_INTERVAL_PATH = "save-interval";

    private static final String KINGDOM_NAME_MIN_LENGTH_PATH = "kingdom-name-min-length";
    private static final String KINGDOM_NAME_MAX_LENGTH_PATH = "kingdom-name-max-length";
    private static final String KINGDOM_DESCRIPTION_MIN_LENGTH_PATH = "kingdom-description-min-length";
    private static final String KINGDOM_DESCRIPTION_MAX_LENGTH_PATH = "kingdom-description-max-length";

    private static final String KINGDOM_MAP_SECTION_PATH = "kingdom-map";
    private static final String KINGDOM_MAP_WIDTH_PATH = "width";
    private static final String KINGDOM_MAP_HEIGHT_PATH = "height";
    private static final String KINGDOM_MAP_WILDERNESS_SYMBOL_PATH = "wilderness-symbol";
    private static final String KINGDOM_MAP_CLAIMED_LAND_SYMBOL_PATH = "kingdom-symbol";
    private static final String KINGDOM_MAP_SELF_CLAIMED_LAND_SYMBOL_PATH = "self-claimed-land-symbol";
    private static final String KINGDOM_MAP_STANDING_IN_SYMBOL_PATH = "standing-in-symbol";

    private static final String COMMANDS_SECTION_PATH = "commands";
    private static final String COMMANDS_PER_HELP_PAGE = "commands-per-help-page";

    private String defaultKingdomDescription;
    private int teleportDelay;
    private int saveInterval;

    private int kingdomNameMinLength;
    private int kingdomNameMaxLength;
    private int kingdomDescriptionMinLength;
    private int kingdomDescriptionMaxLength;

    private int kingdomMapWidth;
    private int kingdomMapHeight;
    private String kingdomMapWildernessSymbol;
    private String kingdomMapClaimedLandSymbol;
    private String kingdomMapSelfClaimedLandSymbol;
    private String kingdomMapStandingInSymbol;

    private int commandsPerHelpPage;

    public void load() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(this.path)
                .build();

        try {
            final var source = loader.load();
            this.defaultKingdomDescription = source.node(DEFAULT_KINGDOM_DESCRIPTION_PATH).getString("");
            this.teleportDelay = source.node(TELEPORT_DELAY_PATH).getInt(3);
            this.saveInterval = source.node(SAVE_INTERVAL_PATH).getInt(20 * 60 * 5);
            this.kingdomNameMinLength = source.node(KINGDOM_NAME_MIN_LENGTH_PATH).getInt(3);
            this.kingdomNameMaxLength = Math.min(source.node(KINGDOM_NAME_MAX_LENGTH_PATH).getInt(16), 255);
            if (this.kingdomNameMaxLength < this.kingdomNameMinLength) {
                this.kingdomNameMaxLength = this.kingdomNameMinLength;
            }
            this.kingdomDescriptionMinLength = source.node(KINGDOM_DESCRIPTION_MIN_LENGTH_PATH).getInt(3);
            this.kingdomDescriptionMaxLength = Math.min(source.node(KINGDOM_DESCRIPTION_MAX_LENGTH_PATH).getInt(255), 255);
            if (this.kingdomDescriptionMaxLength < this.kingdomDescriptionMinLength) {
                this.kingdomDescriptionMaxLength = this.kingdomDescriptionMinLength;
            }
            this.kingdomMapWidth = source.node(KINGDOM_MAP_SECTION_PATH).node(KINGDOM_MAP_WIDTH_PATH).getInt(5);
            this.kingdomMapHeight = source.node(KINGDOM_MAP_SECTION_PATH).node(KINGDOM_MAP_HEIGHT_PATH).getInt(5);
            this.kingdomMapWildernessSymbol = source.node(KINGDOM_MAP_SECTION_PATH).node(KINGDOM_MAP_WILDERNESS_SYMBOL_PATH).getString("<green>□");
            this.kingdomMapClaimedLandSymbol = source.node(KINGDOM_MAP_SECTION_PATH).node(KINGDOM_MAP_CLAIMED_LAND_SYMBOL_PATH).getString("<red>□");
            this.kingdomMapSelfClaimedLandSymbol = source.node(KINGDOM_MAP_SECTION_PATH).node(KINGDOM_MAP_SELF_CLAIMED_LAND_SYMBOL_PATH).getString("<blue>□");
            this.kingdomMapStandingInSymbol = source.node(KINGDOM_MAP_SECTION_PATH).node(KINGDOM_MAP_STANDING_IN_SYMBOL_PATH).getString("<yellow>□");

            this.commandsPerHelpPage = source.node(COMMANDS_SECTION_PATH).node(COMMANDS_PER_HELP_PAGE).getInt(5);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isProperNameLength(String name) {
        final int length = name.length();
        return length >= this.kingdomNameMinLength && length <= this.kingdomNameMaxLength;
    }

    public boolean isProperDescriptionLength(String description) {
        final int length = description.length();
        return length >= this.kingdomDescriptionMinLength && length <= this.kingdomDescriptionMaxLength;
    }

    public String getDefaultKingdomDescription() {
        return this.defaultKingdomDescription;
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public int getKingdomNameMinLength() {
        return kingdomNameMinLength;
    }

    public int getKingdomNameMaxLength() {
        return kingdomNameMaxLength;
    }

    public int getKingdomDescriptionMinLength() {
        return kingdomDescriptionMinLength;
    }

    public int getKingdomDescriptionMaxLength() {
        return kingdomDescriptionMaxLength;
    }

    public int getKingdomMapWidth() {
        return kingdomMapWidth;
    }

    public int getKingdomMapHeight() {
        return kingdomMapHeight;
    }

    public String getKingdomMapWildernessSymbol() {
        return kingdomMapWildernessSymbol;
    }

    public String getKingdomMapClaimedLandSymbol() {
        return kingdomMapClaimedLandSymbol;
    }

    public String getKingdomMapSelfClaimedLandSymbol() {
        return kingdomMapSelfClaimedLandSymbol;
    }

    public String getKingdomMapStandingInSymbol() {
        return kingdomMapStandingInSymbol;
    }

    public int getCommandsPerHelpPage() {
        return commandsPerHelpPage;
    }
}
