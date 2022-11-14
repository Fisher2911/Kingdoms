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

package io.github.fisher2911.kingdoms.command.kingdom.bank;

import io.github.fisher2911.fisherlib.command.CommandSenderType;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BankCommand extends KCommand {

    public BankCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "bank", null, CommandSenderType.PLAYER, 1, 3, subCommands);
        this.addSubCommand(new BalanceSubCommand(plugin, this, new HashMap<>()));
        this.addSubCommand(new DepositSubCommand(plugin, this, new HashMap<>()));
        this.addSubCommand(new WithdrawSubCommand(plugin, this, new HashMap<>()), true);
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.sendHelp(user);
    }

}
