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

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.config.Config;
import io.github.fisher2911.fisherlib.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;

public class AdminSettings extends Config {

    public AdminSettings(FishPlugin<?, ?> plugin) {
        super(plugin, "admin-settings.yml");
    }

    private static final String UPDATE_SETTINGS_PATH = "update-settings";
    private static final String SEND_UPDATE_MESSAGE_TO_CONSOLE_PATH = "send-update-message-to-console";
    private static final String SEND_UPDATE_MESSAGE_ON_JOIN_PATH = "send-update-message-on-join";

    private boolean sendUpdateMessageToConsole;
    private boolean sendUpdateMessageOnJoin;

    public void load() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(this.path)
                .build();

        try {
            final var source = loader.load();
            this.sendUpdateMessageToConsole = source.node(UPDATE_SETTINGS_PATH, SEND_UPDATE_MESSAGE_TO_CONSOLE_PATH).getBoolean(true);
            this.sendUpdateMessageOnJoin = source.node(UPDATE_SETTINGS_PATH, SEND_UPDATE_MESSAGE_ON_JOIN_PATH).getBoolean(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSendUpdateMessageToConsole() {
        return sendUpdateMessageToConsole;
    }

    public boolean isSendUpdateMessageOnJoin() {
        return sendUpdateMessageOnJoin;
    }

}
