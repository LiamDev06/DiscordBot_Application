package net.hybrid.discord;

import com.github.liamhbest0608.CommandAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.hybrid.discord.commands.*;
import net.hybrid.discord.commands.admin.*;
import net.hybrid.discord.commands.staff.ClearMessageCommand;
import net.hybrid.discord.commands.staff.FetchLogsCommand;
import net.hybrid.discord.filters.*;
import net.hybrid.discord.managers.JoinLeaveManager;
import net.hybrid.discord.managers.RolesReaction;
import net.hybrid.discord.managers.VoiceLoungeManager;
import net.hybrid.discord.moderation.ReportReactionListener;
import net.hybrid.discord.systems.BugReports;
import net.hybrid.discord.systems.Suggestions;
import net.hybrid.discord.systems.Support;
import net.hybrid.discord.systems.Verification;
import net.hybrid.discord.utils.DiscordLinkManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DiscordApplication extends JavaPlugin {

    private static DiscordApplication INSTANCE;
    private JDA jda;
    private final Logger LOGGER = getLogger();
    private final String VERSION = getDescription().getVersion();
    public static final String TOKEN = "ODc4Njc4NzE5NjkzMjEzNzM2.YSErUw.0Gro3i8EVKrL9Uzo2jc8MGLD9GM";

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        INSTANCE = this;

        try {
            this.jda = JDABuilder.createDefault(TOKEN)
                    .setActivity(Activity.playing("hybridplays.com"))
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setRawEventsEnabled(true)

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

            jda.awaitReady();
        } catch (LoginException | InterruptedException exception) {
            exception.printStackTrace();
        }

        jda.upsertCommand("addverifyembed", "Sends the official verify embed message to gain server access")
                .addOption(OptionType.CHANNEL, "target", "Specify the channel to send the verify embed", false)
                .queue();

        jda.upsertCommand("addreactionrolesembed", "Sends the official reaction roles embed message to subscribe to server events")
                .addOption(OptionType.CHANNEL, "target", "Specify the channel to send the reaction roles embed", false)
                .queue();

        jda.upsertCommand("channelcontrol", "Main channel control command. View current channel options and sub commands.")

                .addSubcommands(new SubcommandData("chatfilter", "Enable and disable the chat filter")
                        .addOption(OptionType.BOOLEAN, "value", "Chat filter toggle value", true)
                        .addOption(OptionType.CHANNEL, "target", "Select target channel", false))

                .addSubcommands(new SubcommandData("chatlogging", "Enable and disable chat logging")
                        .addOption(OptionType.BOOLEAN, "value", "Chat logging toggle value", true)
                        .addOption(OptionType.CHANNEL, "target", "Select target channel", false))

                .addSubcommands(new SubcommandData("autoslowmode", "Enable and disable the auto slow mode control")
                        .addOption(OptionType.BOOLEAN, "value", "Auto slow mode control toggle value", true)
                        .addOption(OptionType.CHANNEL, "target", "Select target channel", false))

                .addSubcommands(new SubcommandData("staffchannel", "Mark a channel as a staff channel")
                        .addOption(OptionType.BOOLEAN, "value", "Staff channel toggle value", true)
                        .addOption(OptionType.CHANNEL, "target", "Select target channel", false))

                .addSubcommands(new SubcommandData("permanent", "Mark a channel as a permanent channel")
                        .addOption(OptionType.BOOLEAN, "value", "Permanent channel toggle value", true)
                        .addOption(OptionType.CHANNEL, "target", "Select target channel", false))

                .addSubcommands(new SubcommandData("fetch", "View the channel options")
                        .addOption(OptionType.CHANNEL, "target", "Select target channel", false))

                .queueAfter(1, TimeUnit.SECONDS);

        new CommandAPI("!", jda, true);

        new ResetChatCommand();
        new ClearMessageCommand();
        new InformationEmbedCommand();
        new VoicePartyCommand();
        new ReportCommand();
        new ArchiveChannelCommand();
        new FetchLogsCommand();
        new LinkCommand();

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

        this.getServer().getMessenger().registerIncomingPluginChannel(this, "hybrid:discord", new DiscordLinkManager());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "hybrid:discord");
        registerListeners();
        DiscordLinkManager.linkingInProgress = new ArrayList<>();

        LOGGER.info("Hybrid discord application v" + VERSION + " was SUCCESSFULLY enabled in " + (System.currentTimeMillis() - time) + "ms!");
    }

    @Override
    public void onDisable() {
        INSTANCE = null;

        jda.shutdown();
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        LOGGER.info("Hybrid discord application was SUCCESSFULLY shut down.");
    }

    public void registerListeners(){
        jda.addEventListener(new BlacklistedWordsFilter());
        jda.addEventListener(new ChatActionEvents());
        jda.addEventListener(new ChatLogs());
        jda.addEventListener(new JoinLeaveManager());
        jda.addEventListener(new RolesReaction());
        jda.addEventListener(new Suggestions());
        jda.addEventListener(new Support());
        jda.addEventListener(new BugReports());
        jda.addEventListener(new VoiceLoungeManager());
        jda.addEventListener(new ReportReactionListener());
        jda.addEventListener(new MessageLengthFilter());
        jda.addEventListener(new Verification());
        jda.addEventListener(new DiscordLinkManager());
        jda.addEventListener(new LinkBlockerFilter());
        jda.addEventListener(new ChannelControlCommand());
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




