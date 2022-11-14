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
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.api.event.kingdom.KingdomMemberInviteEvent;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class InviteManager {

    private final Kingdoms plugin;
    private final MessageHandler messageHandler;
    private final KingdomManager kingdomManager;
    private final Multimap<UUID, KingdomInvite> invitedPlayers;


    public InviteManager(Kingdoms plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
        this.kingdomManager = this.plugin.getKingdomManager();
        this.invitedPlayers = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
    }

    public void invite(Kingdom kingdom, User inviter, User invited) {
        if (!kingdom.hasPermission(inviter, KPermission.INVITE_MEMBER)) {
            this.messageHandler.sendMessage(invited, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        if (invited.getId().equals(inviter.getId())) {
            this.messageHandler.sendMessage(invited, KMessage.CANNOT_INVITE_SELF);
            return;
        }
        final KingdomInvite invite = new KingdomInvite(kingdom, inviter, invited, Instant.now());
        final Collection<KingdomInvite> invites = this.getInvitedTo(invited.getId());
        if (invites.contains(invite)) {
            this.messageHandler.sendMessage(invited, KMessage.ALREADY_INVITED, invited);
            return;
        }
        final KingdomMemberInviteEvent event = new KingdomMemberInviteEvent(kingdom, inviter, invited);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.invitedPlayers.put(invited.getId(), invite);
        this.messageHandler.sendMessage(inviter, KMessage.INVITED_MEMBER, invited);
        this.messageHandler.sendMessage(invited, KMessage.RECEIVED_INVITE, kingdom, inviter);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (!this.invitedPlayers.containsEntry(invited.getId(), invite)) return;
            this.invitedPlayers.remove(invited.getId(), invite);
            if (!inviter.isOnline()) return;
            this.messageHandler.sendMessage(inviter, KMessage.SENT_KINGDOM_INVITE_EXPIRED, invited);
            this.messageHandler.sendMessage(invited, KMessage.RECEIVED_KINGDOM_INVITE_EXPIRED, inviter, kingdom);
        }, 20 * 60);
    }

    public void tryJoin(KingdomInvite invite) {
        final User invited = invite.invited();
        final User inviter = invite.inviter();
        TaskChain.create(this.plugin)
                .runAsync(() -> this.kingdomManager.join(invited, invite.kingdom().getId(), true).
                        ifPresent(kingdom -> {
                            this.messageHandler.sendMessage(inviter, KMessage.NEW_MEMBER_JOINED_KINGDOM, invited);
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
        this.messageHandler.sendMessage(user, KMessage.NOT_INVITED_TO_KINGDOM);
    }

    public void sendInfo(Kingdom kingdom, User user) {
        this.messageHandler.sendMessage(user, KMessage.KINGDOM_INFO, kingdom);
    }

    public Collection<KingdomInvite> getInvitedTo(UUID invited) {
        return this.invitedPlayers.get(invited);
    }

}
