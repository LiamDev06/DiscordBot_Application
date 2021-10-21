package net.hybrid.discord.utility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.DiscordApplication;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final Guild guild = DiscordApplication.getInstance().getDiscordServer();

    public static boolean hasRole(Member member, String roleName) {
        for (int i=0; i<member.getRoles().size(); i++){
            if (roleName.equals(member.getRoles().get(i).getName())){
                return true;
            }
        }
        return false;
    }

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
                hasRole(member, DiscordRole.HELPER);
    }

    public static boolean isStaffChannel(TextChannel channel) {
        try {
            return channel.getParent().getName().equalsIgnoreCase("logs")
                    || channel.getParent().getName().equalsIgnoreCase("teams")
                    || channel.getParent().getName().equalsIgnoreCase("staff");
        } catch (NullPointerException exception) {
            return false;
        }
    }

    public static TextChannel getStaffCommandsChannel() {
        final List<TextChannel> list = new ArrayList<>(guild.getTextChannelsByName("commands", true));
        TextChannel value = null;

        for (TextChannel channel : list) {
            if (channel.getParent().getName().equalsIgnoreCase("LOGS")) {
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
            if (channel.getParent().getName().equalsIgnoreCase("LOGS")) {
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
            if (channel.getParent().getName().equalsIgnoreCase("LOGS")) {
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
            if (channel.getParent().getName().equalsIgnoreCase("LOGS")) {
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
            if (channel.getParent().getName().equalsIgnoreCase("LOGS")) {
                value = channel;
                break;
            }
        }

        return value;
    }

}
