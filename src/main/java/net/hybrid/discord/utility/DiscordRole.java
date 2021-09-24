package net.hybrid.discord.utility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.hybrid.discord.DiscordApplication;

public class DiscordRole {

    private static final Guild guild = DiscordApplication.getInstance().getDiscordServer();

    public static final Role OWNER = guild.getRoleById(880208064211484688L);
    public static final Role MANAGER = guild.getRoleById(880208064211484688L);
    public static final Role ADMIN = guild.getRoleById(880208064211484688L);
    public static final Role SUPPORT_TEAM = guild.getRoleById(880208064211484688L);
    public static final Role DEVELOPER = guild.getRoleById(880208064211484688L);
    public static final Role MODERATOR = guild.getRoleById(880208064211484688L);
    public static final Role HELPER = guild.getRoleById(880208064211484688L);
    public static final Role TRAINEE = guild.getRoleById(880208064211484688L);
    public static final Role DIAMOND = guild.getRoleById(880208064211484688L);
    public static final Role IRON = guild.getRoleById(880208064211484688L);
    public static final Role MEMBER = guild.getRoleById(880208064211484688L);
    public static final Role STAFF = guild.getRoleById(880208064211484688L);

}
