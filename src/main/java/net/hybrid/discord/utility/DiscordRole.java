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
    public static final Role NITRO_BOOSTER = guild.getRoleById(914439846225903648L);
    public static final Role DIAMOND = guild.getRoleById(891095675259998239L);
    public static final Role IRON = guild.getRoleById(880208064194691147L);
    public static final Role MEMBER = guild.getRoleById(891099834872897566L);
    public static final Role MEMBER_MUTED = guild.getRoleById(901904185430249473L);
    public static final Role LINKED = guild.getRoleById(891097398066167829L);
    public static final Role MUSIC_DJ = guild.getRoleById(891102994895077396L);
    public static final Role SYNC_LOCK = guild.getRoleById(891097327622844467L);
    public static final Role PROOFREADER = guild.getRoleById(906704637372026900L);
    public static final Role TRANSLATOR = guild.getRoleById(906704604572553227L);
    public static final Role PING_UPDATES = guild.getRoleById(898232378743455754L);
    public static final Role PING_EVENTS = guild.getRoleById(882764493933936670L);
    public static final Role PING_SURVIVAL_UPDATES = guild.getRoleById(882764530093002842L);
    public static final Role PING_FFA_UPDATES = guild.getRoleById(916421484195491912L);
    public static final Role BOT_NO_CHAT_FILTER = guild.getRoleById(901904655213285457L);
    public static final Role BOT_NO_CHAT_LOGGING = guild.getRoleById(901904398211485756L);
    public static final Role BOT_PERMANENT_CHANNEL = guild.getRoleById(901904860822274159L);
    public static final Role BOT_STAFF_CHANNEL = guild.getRoleById(901918747500101642L);

}
