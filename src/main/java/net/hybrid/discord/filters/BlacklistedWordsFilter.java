package net.hybrid.discord.filters;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utils.ChatAction;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BlacklistedWordsFilter extends ListenerAdapter {

    public static List<String> blacklistWords;

    public BlacklistedWordsFilter() {
        File file = new File(DiscordApplication.getInstance().getDataFolder(), "blacklisted-words.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        blacklistWords = config.getStringList("words");
    }

    @Override
    @SneakyThrows
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (Utils.hasRole(event.getMember(), DiscordRole.OWNER)
                || Utils.hasRole(event.getMember(), DiscordRole.ADMIN)) return;
        if (Utils.isStaffChannel(event.getChannel())) return;
        if (!Utils.hasChatFilter(event.getChannel())) return;

        String[] messageWords = event.getMessage().getContentRaw().split(" ");

        boolean blacklist = false;
        StringBuilder words = new StringBuilder();

        for (String word : messageWords) {
            word = replace(word);

            if (blacklistWords.contains(word.toLowerCase())) {
                blacklist = true;

                List<String> wordsLIst = Arrays.stream(words.toString().split(" ")).toList();
                if (!wordsLIst.contains(word)) {
                    words.append(word).append("   ");
                }
            }
        }

        if (blacklist) {
            ChatActionEvents.shouldNotSendDeleted.add(event.getMessageId());
            event.getMessage().delete().reason("Contained blacklisted words").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getUser().getEffectiveAvatarUrl());
            embed.addField("Action", ChatAction.BLACKLISTED.name(), true);
            embed.addField("Author", "<@" + event.getMember().getId() + ">", true);
            embed.addField("Message ID", event.getMessageId(), true);
            embed.addField("Channel", "<#" + event.getChannel().getId() + ">", true);
            embed.addBlankField(true);
            embed.addField("Blacklisted Words", words.toString(), true);
            embed.addField("Message", event.getMessage().getContentDisplay(), true);

            Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();
        }
    }

    public static String replace(String input){
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

}



















