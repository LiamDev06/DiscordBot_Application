package net.hybrid.discord.utils;

import net.dv8tion.jda.api.entities.*;
import net.hybrid.discord.DiscordApplication;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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
                hasRole(member, DiscordRole.HELPER);
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

    public static boolean hasAutoSlowMode(TextChannel channel) {
        try {
            assert DiscordRole.BOT_AUTO_SLOW_MODE != null;

            return channel.getPermissionOverride(DiscordRole.BOT_AUTO_SLOW_MODE).isRoleOverride();
        } catch (NullPointerException exception) {
            return false;
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

    public static boolean isMessageBlacklisted(String message) {
        File file = new File(DiscordApplication.getInstance().getDataFolder(), "blacklisted-words.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<String> blacklistWords = config.getStringList("words");

        for (String word : message.trim().split(" ")) {
            word = replace(word);

            if (blacklistWords.contains(word.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private static String replace(String input){
        return input.replace("'", "")
                .replace(".", "")
                .replace(",", "")
                .replace("?", "")
                .replace("!", "")
                .replace("_", " ")
                .replace("-", " ")
                .replace("@", " ")
                .replace("$", "")
                .replace("%", "")
                .replace("&", "")
                .replace("{", "")
                .replace("}", "")
                .replace("(", "")
                .replace(")", "");
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
        return guild.getTextChannelById(891088347936735262L);
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
