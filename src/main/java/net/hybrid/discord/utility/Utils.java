package net.hybrid.discord.utility;

import net.dv8tion.jda.api.entities.*;
import net.hybrid.discord.DiscordApplication;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final Guild guild = DiscordApplication.getInstance().getDiscordServer();

    public static boolean hasRole(Member member, Role discordRole) {
        for (int i=0; i<member.getRoles().size(); i++){
            if (discordRole == member.getRoles().get(i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStaff(Member member){
        return hasRole(member, DiscordRole.OWNER) ||  hasRole(member, DiscordRole.DISCORD_MANAGER) ||
                hasRole(member, DiscordRole.SENIOR_MODERATOR) ||
                hasRole(member, DiscordRole.ADMIN) || hasRole(member, DiscordRole.MODERATOR) ||
                hasRole(member, DiscordRole.HELPER) || hasRole(member, DiscordRole.STAFF);
    }

    public static boolean isStaffChannel(TextChannel channel) {
        try {
            assert DiscordRole.BOT_STAFF_CHANNEL != null;

            return channel.getPermissionOverride(DiscordRole.BOT_STAFF_CHANNEL).isRoleOverride();
        } catch (NullPointerException exception) {
            return false;
        }
    }

    public static boolean hasChatLogging(TextChannel channel) {
        try {
            assert DiscordRole.BOT_NO_CHAT_LOGGING != null;

            return !channel.getPermissionOverride(DiscordRole.BOT_NO_CHAT_LOGGING).isRoleOverride();
        } catch (NullPointerException exception) {
            return true;
        }
    }

    public static boolean hasChatFilter(TextChannel channel) {
        try {
            assert DiscordRole.BOT_NO_CHAT_FILTER != null;

            return !channel.getPermissionOverride(DiscordRole.BOT_NO_CHAT_FILTER).isRoleOverride();
        } catch (NullPointerException exception) {
            return true;
        }
    }
    public static boolean isPermanentChannel(GuildChannel channel) {
        try {
            assert DiscordRole.BOT_PERMANENT_CHANNEL != null;

            return channel.getPermissionOverride(DiscordRole.BOT_PERMANENT_CHANNEL).isRoleOverride();
        } catch (NullPointerException exception) {
            return false;
        }
    }

    public static User getUserFromID(String userId) {
        return DiscordApplication.getInstance().getJda().retrieveUserById(userId).complete();
    }

    public static User getUserFromID(long userId) {
        return DiscordApplication.getInstance().getJda().retrieveUserById(userId).complete();
    }

    public static Member getUserFromMember(User user) {
        return DiscordApplication.getInstance().getDiscordServer().getMember(user);
    }

    public static TextChannel getStaffCommandsChannel() {
        final List<TextChannel> list = new ArrayList<>(guild.getTextChannelsByName("commands", true));
        TextChannel value = null;

        for (TextChannel channel : list) {
            if (channel.getParent().getName().toUpperCase().contains("LOGS")) {
                value = channel;
                break;
            }
        }

        return value;
    }

    public static TextChannel getDiscordLogsChannel() {
        final List<TextChannel> list = new ArrayList<>(guild.getTextChannelsByName("discord", true));
        TextChannel value = null;

        for (TextChannel channel : list) {
            if (channel.getParent().getName().toUpperCase().contains("LOGS")) {
                value = channel;
                break;
            }
        }

        return value;
    }

    public static TextChannel getServerLogsChannel() {
        final List<TextChannel> list = new ArrayList<>(guild.getTextChannelsByName("minecraft", true));
        TextChannel value = null;

        for (TextChannel channel : list) {
            if (channel.getParent().getName().toUpperCase().contains("LOGS")) {
                value = channel;
                break;
            }
        }

        return value;
    }

    public static TextChannel getPunishmentLogsChannel() {
        final List<TextChannel> list = new ArrayList<>(guild.getTextChannelsByName("punishments", true));
        TextChannel value = null;

        for (TextChannel channel : list) {
            if (channel.getParent().getName().toUpperCase().contains("LOGS")) {
                value = channel;
                break;
            }
        }

        return value;
    }

    public static TextChannel getUserReportsChannel() {
        final List<TextChannel> list = new ArrayList<>(guild.getTextChannelsByName("reports", true));
        TextChannel value = null;

        for (TextChannel channel : list) {
            if (channel.getParent().getName().contains("LOGS")) {
                value = channel;
                break;
            }
        }

        return value;
    }

    public static TextChannel liamDevDebugChannel() {
        final List<TextChannel> list = new ArrayList<>(guild.getTextChannelsByName("liam-dev", true));
        TextChannel value = null;

        for (TextChannel channel : list) {
            if (channel.getParent().getName().contains("Owner Lounge")) {
                value = channel;
                break;
            }
        }

        return value;
    }

}
