package net.hybrid.discord.utils;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.hybridplays.core.CorePlugin;
import com.hybridplays.core.data.Mongo;
import com.hybridplays.core.utility.CC;
import com.hybridplays.core.utility.enums.PlayerRank;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.DiscordApplication;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DiscordLinkManager extends ListenerAdapter implements PluginMessageListener {

    public static ArrayList<Long> linkingInProgress;

    public static void executeLinkActions(Document document, Member member) {
        linkingInProgress.remove(member.getIdLong());
        linkingInProgress.add(member.getIdLong());

        Guild guild = DiscordApplication.getInstance().getDiscordServer();
        Mongo mongo = CorePlugin.getInstance().getMongo();

        if (DiscordRole.SYNC_LOCK != null && !Utils.hasRole(member, DiscordRole.SYNC_LOCK)) {
            if (DiscordRole.VERIFIED != null) {
                guild.removeRoleFromMember(member.getId(), DiscordRole.VERIFIED).reason("USER LINKED >> Removed the verified role").queueAfter(1, TimeUnit.SECONDS);
            }

            if (DiscordRole.LINKED != null) {
                guild.addRoleToMember(member.getId(), DiscordRole.LINKED).reason("USER LINKED >> Added the linked role").queueAfter(2, TimeUnit.SECONDS);
            }

            Document playerDocument = mongo.loadDocumentWithNull("playerData", UUID.fromString(document.getString("playerUuid")));
            if (playerDocument != null) {
                for (PlayerRank rank : PlayerRank.values()) {
                    if (Utils.hasRole(member, guild.getRoleById(rank.getLinkedDiscordRoleId())) && guild.getRoleById(rank.getLinkedDiscordRoleId()).getIdLong() != PlayerRank.OWNER.getLinkedDiscordRoleId()) {
                        guild.removeRoleFromMember(member, guild.getRoleById(rank.getLinkedDiscordRoleId())).reason("USER LINKED >> Role cleanup").queue();
                    }
                }

                Role playerRankRole = null;
                Role specialRankRole = null;
                Role staffRankRole = null;

                if (!playerDocument.getString("playerRank").equalsIgnoreCase("")) {
                    playerRankRole = guild.getRoleById(PlayerRank.valueOf(playerDocument.getString("playerRank").toUpperCase()).getLinkedDiscordRoleId());
                }

                if (!playerDocument.getString("specialRank").equalsIgnoreCase("")) {
                    specialRankRole = guild.getRoleById(PlayerRank.valueOf(playerDocument.getString("specialRank").toUpperCase()).getLinkedDiscordRoleId());
                }

                if (!playerDocument.getString("staffRank").equalsIgnoreCase("")) {
                    staffRankRole = guild.getRoleById(PlayerRank.valueOf(playerDocument.getString("staffRank").toUpperCase()).getLinkedDiscordRoleId());
                }

                if (staffRankRole != null && staffRankRole.getIdLong() != PlayerRank.OWNER.getLinkedDiscordRoleId()) {
                    guild.addRoleToMember(member.getId(), staffRankRole).reason("USER LINKED >> Added staff rank role").queue();
                }

                if (specialRankRole != null) {
                    guild.addRoleToMember(member.getId(), specialRankRole).reason("USER LINKED >> Added special rank role").queue();
                }

                if (playerRankRole != null) {
                    guild.addRoleToMember(member.getId(), playerRankRole).reason("USER LINKED >> Added player rank role").queue();
                }

                if (!Utils.hasRole(member, DiscordRole.OWNER)) {
                    String playerName = Bukkit.getOfflinePlayer(UUID.fromString(document.getString("playerUuid"))).getName();
                    String fullNick = playerName + " [" + playerName + "]";

                    if (fullNick.length() <= 32) {
                        guild.modifyNickname(member, fullNick).reason("USER LINKED >> Added the player's IGN to their discord nick").queue();
                    } else {
                        guild.modifyNickname(member, playerName.substring(0, 3) + " [" + playerName + "]").reason("USER LINKED >> Added the player's IGN to their discord nick").queue();
                    }
                }
            }
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("DiscordLinkExecuted");
        out.writeUTF("ONLINE");
        out.writeUTF("DiscordLinkExecuted");

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(msgBytes);

        try {
            msgOut.writeUTF(member.getEffectiveName());
            msgOut.writeUTF(member.getUser().getDiscriminator());
            msgOut.writeUTF(document.getString("playerUuid"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        out.write(msgBytes.toByteArray()); // Discord User Name
        out.write(msgBytes.toByteArray()); // Discord User #
        out.write(msgBytes.toByteArray()); // Player Uuid

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null) {
            player.sendPluginMessage(CorePlugin.getInstance(), "BungeeCord", out.toByteArray());
        }

        linkingInProgress.remove(member.getIdLong());
    }

    @Override
    public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent event) {
        if (linkingInProgress.contains(event.getMember().getIdLong())) return;
        if (isSyncLocked(event.getMember())) return;
        if (Utils.hasRole(event.getMember(), DiscordRole.OWNER)) return;
        Document document = getDiscordLinkDoc(event.getMember());
        if (document == null) return;

        String playerName = Bukkit.getOfflinePlayer(UUID.fromString(document.getString("playerUuid"))).getName();

        if (event.getNewNickname() == null && Utils.hasRole(event.getMember(), DiscordRole.OWNER)) {
            String fullNick = playerName + " [" + playerName + "]";

            if (fullNick.length() <= 32) {
                event.getGuild().modifyNickname(event.getMember(), fullNick).reason("USER LINKED >> Added the player's IGN to their discord nick").queue();
            } else {
                event.getGuild().modifyNickname(event.getMember(), playerName.substring(0, 3) + " [" + playerName + "]").reason("USER LINKED >> Added the player's IGN to their discord nick").queue();
            }
            return;
        }

        handleNick(event.getNewNickname(), event.getMember(), playerName);
    }

    @Override
    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
        if (linkingInProgress.contains(event.getMember().getIdLong())) return;
        if (isSyncLocked(event.getMember())) return;

        Document discordLinkDoc = getDiscordLinkDoc(event.getMember());
        if (discordLinkDoc == null) return;
        Document document = CorePlugin.getInstance().getMongo().loadDocumentWithNull("playerData", UUID.fromString((discordLinkDoc.getString("playerUuid"))));
        if (document == null) return;

        for (PlayerRank rank : PlayerRank.values()) {
            for (Role role : event.getRoles()) {

                if (rank.getLinkedDiscordRoleId() == role.getIdLong() && rank != PlayerRank.OWNER) {
                    // A role that corresponds to a rank was added, check if player has that rank

                    if (!document.getString("playerRank").equalsIgnoreCase(rank.name())
                        && !document.getString("specialRank").equalsIgnoreCase(rank.name())
                        && !document.getString("staffRank").equalsIgnoreCase(rank.name())) {
                        event.getGuild().removeRoleFromMember(event.getMember().getId(), role).reason("LINK SYSTEM >> User added a role that did not correspond to their rank so removed it").queue();
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMemberRoleRemove(@Nonnull GuildMemberRoleRemoveEvent event) {
        if (linkingInProgress.contains(event.getMember().getIdLong())) return;
        if (isSyncLocked(event.getMember())) return;

        Document discordLinkDoc = getDiscordLinkDoc(event.getMember());
        if (discordLinkDoc == null) return;
        Document document = CorePlugin.getInstance().getMongo().loadDocumentWithNull("playerData", UUID.fromString((discordLinkDoc.getString("playerUuid"))));
        if (document == null) return;

        for (PlayerRank rank : PlayerRank.values()) {
            for (Role role : event.getRoles()) {

                if (rank.getLinkedDiscordRoleId() == role.getIdLong() && rank != PlayerRank.OWNER) {
                    // A role that corresponds to a rank was removed, check if player has that rank

                    if (document.getString("playerRank").equalsIgnoreCase(rank.name())
                            || document.getString("specialRank").equalsIgnoreCase(rank.name())
                            || document.getString("staffRank").equalsIgnoreCase(rank.name())) {
                        event.getGuild().addRoleToMember(event.getMember().getId(), role).reason("LINK SYSTEM >> User removed a role that corresponds to one of their ranks so added it back").queue();
                    }
                }
            }
        }

        if (!Utils.hasRole(event.getMember(), DiscordRole.SYNC_LOCK)) {
             String playerName = Bukkit.getOfflinePlayer(UUID.fromString(document.getString("playerUuid"))).getName();
             handleNick(event.getMember().getEffectiveName(), event.getMember(), playerName);

             for (PlayerRank rank : PlayerRank.values()) {
                if (rank != PlayerRank.OWNER) {
                    if (rank.name().equalsIgnoreCase(document.getString("playerRank")) ||
                            rank.name().equalsIgnoreCase(document.getString("specialRank")) ||
                            rank.name().equalsIgnoreCase(document.getString("staffRank"))) {
                        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(rank.getLinkedDiscordRoleId())).reason("LINK SYSTEM >> Role cleanup because Sync Lock effect was removed").queue();
                    }
                }
             }

             for (PlayerRank rank : PlayerRank.values()) {
                if (event.getMember().getRoles().contains(event.getGuild().getRoleById(rank.getLinkedDiscordRoleId())) && rank != PlayerRank.OWNER) {
                    // The member has a role that is equal to a rank

                    if (!document.getString("playerRank").equalsIgnoreCase(rank.name())
                            && !document.getString("specialRank").equalsIgnoreCase(rank.name())
                            && !document.getString("staffRank").equalsIgnoreCase(rank.name())) {
                        event.getGuild().removeRoleFromMember(event.getMember().getId(), event.getGuild().getRoleById(rank.getLinkedDiscordRoleId())).reason("LINK SYSTEM >> Role cleanup because Sync Lock was removed").queue();
                    }
                }
             }
        }
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        if (event.getMember() == null) return;
        CorePlugin.getInstance().getMongo().deleteDocument("discordLinks", getDiscordLinkDoc(event.getMember()));
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("hybrid:discord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equals("PlayerRankUpdate_ForwardFromBungee")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String rankName = in.readUTF();
            final String uuidValue = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("PlayerRankUpdate_ForwardFromBungee")) return;

            UUID targetUuid = UUID.fromString(uuidValue);
            PlayerRank playerRank = PlayerRank.valueOf(rankName.toUpperCase());
            Guild discordServer = DiscordApplication.getInstance().getDiscordServer();

            Document discordLinkDoc = getDiscordLinkDoc(targetUuid);
            if (discordLinkDoc == null) return;
            Document document = CorePlugin.getInstance().getMongo().loadDocumentWithNull("playerData", UUID.fromString((discordLinkDoc.getString("playerUuid"))));
            if (document == null) return;

            String discordId = discordLinkDoc.getString("discordId");

           if (playerRank == PlayerRank.PARTNER || playerRank == PlayerRank.YOUTUBER || playerRank == PlayerRank.TWITCH_STREAMER) {
               for (PlayerRank rank : PlayerRank.values()) {
                   if (rank.isSpecialRank() && rank != PlayerRank.OWNER) {
                       discordServer.removeRoleFromMember(discordId, Objects.requireNonNull(discordServer.getRoleById(rank.getLinkedDiscordRoleId()))).reason("LINK SYSTEM >> Automatic role update due to server-side rank change").queue();
                   }
               }
            } else {
               for (PlayerRank rank : PlayerRank.values()) {
                   if (rank.isPlayerRank() && rank != PlayerRank.OWNER) {
                       discordServer.removeRoleFromMember(discordId, Objects.requireNonNull(discordServer.getRoleById(rank.getLinkedDiscordRoleId()))).reason("LINK SYSTEM >> Automatic role update due to server-side rank change").queue();
                   }
               }
            }

            for (PlayerRank rank : PlayerRank.values()) {
                if (rank.isStaffRank() && rank != PlayerRank.OWNER) {
                    discordServer.removeRoleFromMember(discordId, Objects.requireNonNull(discordServer.getRoleById(rank.getLinkedDiscordRoleId()))).reason("LINK SYSTEM >> Automatic role update due to server-side rank change").queue();
                }
            }

            assert DiscordRole.DISCORD_MANAGER != null;
            if (playerRank != PlayerRank.OWNER) {
                discordServer.addRoleToMember(discordId, Objects.requireNonNull(discordServer.getRoleById(playerRank.getLinkedDiscordRoleId()))).reason("LINK SYSTEM >> Automatic role update due to server-side rank change").queue();
            }
            discordServer.removeRoleFromMember(discordId, DiscordRole.DISCORD_MANAGER).reason("LINK SYSTEM >> Automatic role update due to server-side rank change").queue();

            Utils.getDiscordLogsChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.MAGENTA)
                    .setTitle("Automatic Role Update")
                    .appendDescription("A linked user's rank was changed server-side and therefore was automatically updated in the discord.\n\n")
                    .appendDescription("**Discord User:** <@" + discordId + ">\n")
                    .appendDescription("**Player:** " + Bukkit.getOfflinePlayer(UUID.fromString(discordLinkDoc.getString("playerUuid"))).getName() + " (`" + discordLinkDoc.getString("playerUuid") + "`)\n")
                    .appendDescription("**Rank Set:** " + CC.decolor(playerRank.getDisplayName()) + " (<@&" + playerRank.getLinkedDiscordRoleId() + ">)\n\n")
                    .appendDescription("**Timestamp:** <t:" + Instant.now().getEpochSecond() + ":R>")
                    .build()).queue();
        }

        if (subChannel.equals("PlayerDiscordUnlink_ForwardFromBungee")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String uuidValue = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("PlayerDiscordUnlink_ForwardFromBungee")) return;

            Document document = CorePlugin.getInstance().getMongo().getCoreDatabase().getCollection("discordLinks").find(Filters.eq("playerUuid", uuidValue)).first();
            if (document == null) return;

            Guild guild = DiscordApplication.getInstance().getDiscordServer();
            Member member = guild.getMemberById(document.getString("discordId"));
            CorePlugin.getInstance().getMongo().deleteDocument("discordLinks", document);

            if (member == null) return;
            if (!Utils.hasRole(member, DiscordRole.OWNER)) {
                member.modifyNickname(member.getUser().getName()).reason("USER UNLINKED >> Reset nickname back to normal").queue();
            }

            guild.removeRoleFromMember(member, DiscordRole.LINKED).reason("USER UNLINKED >> Removed link specific roles").queue();
            guild.removeRoleFromMember(member, DiscordRole.SYNC_LOCK).reason("USER UNLINKED >> Removed link specific roles").queue();
            guild.removeRoleFromMember(member, DiscordRole.DISCORD_MANAGER).reason("USER UNLINKED >> Removed link specific roles").queue();

            for (PlayerRank rank : PlayerRank.values()) {
                if (rank != PlayerRank.OWNER) {
                    guild.removeRoleFromMember(member, guild.getRoleById(rank.getLinkedDiscordRoleId())).reason("USER UNLINKED >> Removed rank corresponding roles").queue();
                }
            }

            guild.addRoleToMember(member, DiscordRole.VERIFIED).reason("USER LINKED >> Re-added verified role, replacing linked with verified").queue();
        }
    }

    private void handleNick(String nickname, Member member, String playerName) {
        if (Utils.hasRole(member, DiscordRole.OWNER)) return;
        String fullPlayerName = "[" + playerName + "]";

        if (nickname == null) {
            String fullNick = playerName + " [" + playerName + "]";

            if (fullNick.length() <= 32) {
                member.modifyNickname(fullNick).reason("LINK SYSTEM >> Added the player's IGN to their discord nick").queue();
            } else {
                member.modifyNickname(playerName.substring(0, 3) + " [" + playerName + "]").reason("LINK SYSTEM >> Added the player's IGN to their discord nick").queue();
            }
            return;
        }

        if (!nickname.endsWith(" " + fullPlayerName)) {
            String rawNickname = nickname.replace(fullPlayerName, "").trim();
            String newName = rawNickname + " " + fullPlayerName;

            if (newName.length() > 32) {
                for (int i = 1; i < 34; i++) {
                    newName = rawNickname.substring(0, rawNickname.length() - i).trim() + " " + fullPlayerName;

                    if (newName.length() <= 32) {
                        break;
                    }
                }
            }

            member.modifyNickname(newName).reason("LINK SYSTEM >> Added the player's IGN again after they changed their nick").queue();
        }
    }

    private static boolean isSyncLocked(Member member) {
        return Utils.hasRole(member, DiscordRole.SYNC_LOCK);
    }

    private static Document getDiscordLinkDoc(Member member) {
        return CorePlugin.getInstance().getMongo().loadDocument("discordLinks", "discordId", member.getId());
    }

    private static Document getDiscordLinkDoc(UUID uuid) {
        return CorePlugin.getInstance().getMongo().loadDocument("discordLinks", "playerUuid", uuid.toString());
    }

}
















