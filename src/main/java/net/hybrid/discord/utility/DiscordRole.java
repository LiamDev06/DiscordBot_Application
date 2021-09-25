package net.hybrid.discord.utility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.hybrid.discord.DiscordApplication;

public class DiscordRole {

    private static final Guild guild = DiscordApplication.getInstance().getDiscordServer();

    public static final Role OWNER = guild.getRoleById(880208064211484688L);
    public static final Role DISCORD_MANAGER = guild.getRoleById(891106412565000243L);
    public static final Role ADMIN = guild.getRoleById(880208064211484686L);
    public static final Role SENIOR_MODERATOR = guild.getRoleById(891095405700452362L);
    public static final Role MODERATOR = guild.getRoleById(880208064211484684L);
    public static final Role HELPER = guild.getRoleById(880208064211484683L);
    public static final Role PARTNER = guild.getRoleById(880208064194691149L);
    public static final Role YOUTUBER = guild.getRoleById(891096304216842240L);
    public static final Role TWITCH_STREAMER = guild.getRoleById(891096289163505724L);
    public static final Role DIAMOND = guild.getRoleById(891095675259998239L);
    public static final Role IRON = guild.getRoleById(880208064194691147L);
    public static final Role MEMBER = guild.getRoleById(880208064194691146L);
    public static final Role VERIFIED = guild.getRoleById(891099834872897566L);
    public static final Role LINKED = guild.getRoleById(891097398066167829L);
    public static final Role PING_EVENTS = guild.getRoleById(882764493933936670L);
    public static final Role PING_SMP_UPDATES = guild.getRoleById(882764530093002842L);
    public static final Role STAFF = guild.getRoleById(880208064194691145L);
    public static final Role MUSIC_BOT_ACCESS = guild.getRoleById(891102994895077396L);
    public static final Role BOTS = guild.getRoleById(880208064211484682L);
    public static final Role CHAT_BYPASS = guild.getRoleById(891108005536481352L);
    public static final Role SYNC_LOCK = guild.getRoleById(891097327622844467L);
    public static final Role UNVERIFIED = guild.getRoleById(891102923948437514L);
}