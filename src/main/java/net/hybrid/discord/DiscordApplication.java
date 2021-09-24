package net.hybrid.discord;

import com.github.liamhbest0608.CommandAPI;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.hybrid.discord.commands.ClearMessageCommand;
import net.hybrid.discord.commands.ResetChatCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DiscordApplication extends JavaPlugin {

    private static DiscordApplication INSTANCE;
    private JDA jda;
    private final Logger LOGGER = getLogger();
    private final String VERSION = getDescription().getVersion();
    private final String TOKEN = "ODc4Njc4NzE5NjkzMjEzNzM2.YSErUw.0Gro3i8EVKrL9Uzo2jc8MGLD9GM";

    private Guild guild;

    @Override
    @SneakyThrows
    public void onEnable() {
        long time = System.currentTimeMillis();
        INSTANCE = this;

        this.jda = JDABuilder.createDefault(this.TOKEN)
                .setActivity(Activity.playing("Hybrid Network"))
                .build();

        this.guild = jda.getGuildById(880208064194691143L);

        new CommandAPI("!", jda, true);
        new ResetChatCommand();
        new ClearMessageCommand();

        LOGGER.info("Hybrid discord application v" + VERSION + " was SUCCESSFULLY enabled in " + (System.currentTimeMillis() - time) + "ms!");
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
        jda.shutdown();
        LOGGER.info("Hybrid discord application was SUCCESSFULLY shut down.");
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
        return guild;
    }

}




