package net.hybrid.discord.utility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.DiscordApplication;

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
        if (hasRole(member, DiscordRole.OWNER) || hasRole(member, DiscordRole.MANAGER) ||
                hasRole(member, DiscordRole.ADMIN) || hasRole(member, DiscordRole.SUPPORT_TEAM) ||
                hasRole(member, DiscordRole.DEVELOPER) || hasRole(member, DiscordRole.MODERATOR) ||
                hasRole(member, DiscordRole.HELPER)) {
            return true;
        } else {
            return false;
        }
    }

    public static TextChannel getStaffCommandsChannel() {
        return guild.getTextChannelsByName("staff-commands", true).get(0);
    }

    public static TextChannel getDiscordLogsChannel() {
        return guild.getTextChannelsByName("discord-logs", true).get(0);
    }

    public static TextChannel getServerLogsChannel() {
        return guild.getTextChannelsByName("server-logs", true).get(0);
    }

    public static TextChannel getPunishmentLogsChannel() {
        return guild.getTextChannelsByName("punishments-logs", true).get(0);
    }

}
