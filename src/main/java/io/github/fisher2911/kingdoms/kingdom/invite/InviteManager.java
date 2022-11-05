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

package io.github.fisher2911.kingdoms.kingdom.invite;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class InviteManager {

    private final Kingdoms plugin;
    private final KingdomManager kingdomManager;
    private final Multimap<UUID, KingdomInvite> invitedPlayers;


    public InviteManager(Kingdoms plugin) {
        this.plugin = plugin;
        this.kingdomManager = this.plugin.getKingdomManager();
        this.invitedPlayers = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
    }

    public void invite(Kingdom kingdom, User inviter, User invited) {
        if (!kingdom.hasPermission(inviter, KPermission.INVITE_MEMBER)) {
            MessageHandler.sendMessage(invited, Message.NO_KINGDOM_PERMISSION);
            return;
        }
        if (invited.getId().equals(inviter.getId())) {
            MessageHandler.sendMessage(invited, Message.CANNOT_INVITE_SELF);
            return;
        }
        final KingdomInvite invite = new KingdomInvite(kingdom, inviter, invited, Instant.now());
        final Collection<KingdomInvite> invites = this.getInvitedTo(invited.getId());
        if (invites.contains(invite)) {
            MessageHandler.sendMessage(invited, Message.ALREADY_INVITED, invited);
            return;
        }
        this.invitedPlayers.put(invited.getId(), invite);
        MessageHandler.sendMessage(inviter, Message.INVITED_MEMBER, invited);
        MessageHandler.sendMessage(invited, Message.RECEIVED_INVITE, kingdom, inviter);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (!this.invitedPlayers.containsEntry(invited.getId(), invite)) return;
            this.invitedPlayers.remove(invited.getId(), invite);
            if (!inviter.isOnline()) return;
            MessageHandler.sendMessage(inviter, Message.SENT_KINGDOM_INVITE_EXPIRED, invited);
            MessageHandler.sendMessage(invited, Message.RECEIVED_KINGDOM_INVITE_EXPIRED, inviter, kingdom);
        }, 20 * 60);
    }

    public void tryJoin(KingdomInvite invite) {
        final User invited = invite.invited();
        final User inviter = invite.inviter();
        TaskChain.create(this.plugin)
                .runAsync(() -> this.kingdomManager.join(invited, invite.kingdom().getId(), true).
                        ifPresent(kingdom -> {
                            MessageHandler.sendMessage(inviter, Message.NEW_MEMBER_JOINED_KINGDOM, invited);
                            this.invitedPlayers.remove(invited.getId(), invite);
                        }))
                .execute();
    }

    public void tryJoin(User user, String name) {
        for (KingdomInvite invite : this.invitedPlayers.get(user.getId())) {
            if (!invite.kingdom().getName().equalsIgnoreCase(name)) continue;
            this.tryJoin(invite);
            return;
        }
        MessageHandler.sendMessage(user, Message.NOT_INVITED_TO_KINGDOM);
    }

    public void sendInfo(Kingdom kingdom, User user) {
        MessageHandler.sendMessage(user, Message.KINGDOM_INFO, kingdom);
    }

    public Collection<KingdomInvite> getInvitedTo(UUID invited) {
        return this.invitedPlayers.get(invited);
    }
}
