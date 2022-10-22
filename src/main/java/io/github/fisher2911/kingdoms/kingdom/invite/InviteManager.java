package io.github.fisher2911.kingdoms.kingdom.invite;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
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
        final KingdomInvite invite = new KingdomInvite(kingdom, inviter, invited, Instant.now());
        final Collection<KingdomInvite> invites = this.getInvitedTo(invited.getId());
        if (invites.contains(invite)) {
            MessageHandler.sendMessage(invited, Message.ALREADY_INVITED);
            return;
        }
        this.invitedPlayers.put(invited.getId(), invite);
        MessageHandler.sendMessage(inviter, Message.INVITED_MEMBER);
        MessageHandler.sendMessage(invited, Message.RECEIVED_INVITE);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.invitedPlayers.remove(invited.getId(), invite);
            if (!inviter.isOnline()) return;
            MessageHandler.sendMessage(inviter, Message.KINGDOM_INVITE_EXPIRED);
            MessageHandler.sendMessage(invited, Message.KINGDOM_INVITE_EXPIRED);
        }, 20 * 60);
    }

    public void tryJoin(KingdomInvite invite) {
        final User invited = invite.invited();
        final User inviter = invite.inviter();
        this.kingdomManager.join(invited, invite.kingdom().getId()).
                ifPresent(kingdom -> MessageHandler.sendMessage(inviter, Message.NEW_MEMBER_JOINED_KINGDOM));
    }

    public void tryJoin(User user, String name) {
        for (KingdomInvite invite : this.invitedPlayers.get(user.getId())) {
            if (!invite.kingdom().getName().equalsIgnoreCase(name)) continue;
            this.tryJoin(invite);
            return;
        }
        MessageHandler.sendMessage(user, Message.NOT_INVITED_TO_KINGDOM);
    }

    public Collection<KingdomInvite> getInvitedTo(UUID invited) {
        return this.invitedPlayers.get(invited);
    }
}