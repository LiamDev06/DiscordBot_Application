package net.hybrid.discord;

import com.github.liamhbest0608.CommandAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.hybrid.discord.commands.ClearMessageCommand;
import net.hybrid.discord.commands.ForceVerifyCommand;
import net.hybrid.discord.commands.ResetChatCommand;
import net.hybrid.discord.filters.BlacklistedWordsFilter;
import net.hybrid.discord.filters.ChatActionEvents;
import net.hybrid.discord.filters.ChatLogs;
import net.hybrid.discord.managers.JoinLeaveManager;
import net.hybrid.discord.managers.RolesReaction;
import net.hybrid.discord.managers.VerifyManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

public class DiscordApplication extends JavaPlugin {

    private static DiscordApplication INSTANCE;
    private JDA jda;
    private final Logger LOGGER = getLogger();
    private final String VERSION = getDescription().getVersion();
    private final String TOKEN = "ODc4Njc4NzE5NjkzMjEzNzM2.YSErUw.0Gro3i8EVKrL9Uzo2jc8MGLD9GM";

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        INSTANCE = this;

        try {
            this.jda = JDABuilder.createDefault(this.TOKEN)
                    .setActivity(Activity.playing("Hybrid Network"))
                    .setMemberCachePolicy(MemberCachePolicy.ALL)

                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableIntents(GatewayIntent.GUILD_BANS)
                    .enableIntents(GatewayIntent.GUILD_EMOJIS)
                    .enableIntents(GatewayIntent.GUILD_WEBHOOKS)
                    .enableIntents(GatewayIntent.GUILD_INVITES)
                    .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_TYPING)
                    .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                    .enableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                    .enableIntents(GatewayIntent.DIRECT_MESSAGE_TYPING)

                    .enableCache(CacheFlag.MEMBER_OVERRIDES)

                    .build();
        } catch (LoginException exception) {
            exception.printStackTrace();
        }

        new CommandAPI("!", jda, true);
        new ResetChatCommand();
        new ClearMessageCommand();
        new ForceVerifyCommand();

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        try {
            File badWordsFile = new File(getDataFolder(), "blacklisted-words.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(badWordsFile);
            config.options().copyDefaults(true);
            config.save(badWordsFile);

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        registerListeners();

        LOGGER.info("Hybrid discord application v" + VERSION + " was SUCCESSFULLY enabled in " + (System.currentTimeMillis() - time) + "ms!");
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
        jda.shutdown();
        LOGGER.info("Hybrid discord application was SUCCESSFULLY shut down.");
    }

    public void registerListeners(){
        jda.addEventListener(new BlacklistedWordsFilter());
        jda.addEventListener(new ChatActionEvents());
        jda.addEventListener(new ChatLogs());
        jda.addEventListener(new JoinLeaveManager());
        jda.addEventListener(new VerifyManager());
        jda.addEventListener(new RolesReaction());
    }

    public static DiscordApplication getInstance(){
        return INSTANCE;
    }

    public JDA getJda(){
        return jda;
    }

    public Logger getPluginLogger(){
        return LOGGER;
    }

    public Guild getDiscordServer(){
        return jda.getGuildById(880208064194691143L);
    }

}




