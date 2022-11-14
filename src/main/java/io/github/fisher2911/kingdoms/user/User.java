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

package io.github.fisher2911.kingdoms.user;

import io.github.fisher2911.fisherlib.data.Saveable;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.command.CommandPermission;

public interface User extends CoreUser, Saveable {

    User CONSOLE = new ConsoleUser();

    int getKingdomId();
    void setKingdomId(int id);
    boolean hasKingdom();
    ChatChannel getChatChannel();
    void setChatChannel(ChatChannel chatChannel);
    boolean hasPermission(CommandPermission permission);

}
