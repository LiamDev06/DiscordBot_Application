package net.hybrid.discord.filters;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.DiscordApplication;

import javax.annotation.Nonnull;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ChatLogs extends ListenerAdapter {

    @Override @SneakyThrows
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!shouldLog(event.getChannel())) return;
        if (event.getAuthor().isSystem()) return;
        if (event.getAuthor().isBot()) return;

        DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String year = yearFormat.format(now);
        String month = monthFormat.format(now);
        String day = dayFormat.format(now);
        String time = timeFormat.format(now);

        File file = new File(DiscordApplication.getInstance().getDataFolder() + "/logs"
         + "/#" + event.getChannel().getName(), year + "-" + month + "-" + day + "_log.log");

        String content = "[" + event.getMember().getEffectiveName() + " - " + time + " GMT - " + event.getMessageId() + "] " + '"' + event.getMessage().getContentStripped() + '"';
        FileWriter writer = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(writer);

        bw.write(content);
        bw.newLine();

        bw.flush();
        bw.close();

        File files[] = new File(DiscordApplication.getInstance().getDataFolder() +
                "/logs/#" + event.getChannel().getName()).listFiles();

        if (files.length >= 5) {
            Arrays.stream(files).findFirst().get().delete();
        }
    }

    public boolean shouldLog(TextChannel textChannel){
        return textChannel.getName().equalsIgnoreCase("minecraft-talk");
        /**
        return  (textChannel.getName().equalsIgnoreCase("verify")
                || textChannel.getName().equalsIgnoreCase("\uD83D\uDC4B-welcome-\uD83D\uDC4B")
                || textChannel.getParent().getName().equalsIgnoreCase("INFORMATION")
                || textChannel.getParent().getName().equalsIgnoreCase("\uD83D\uDCDD Misc \uD83D\uDCDD")
                || textChannel.getName().equalsIgnoreCase("music-control")
                || textChannel.getParent().getName().equalsIgnoreCase("TICKETS")
                || textChannel.getParent().getName().equalsIgnoreCase("BUG REPORTS")
                || textChannel.getParent().getName().equalsIgnoreCase("SUGGESTIONS")
                || textChannel.getParent().getName().equalsIgnoreCase("APPLICATIONS")
                || textChannel.getParent().getName().equalsIgnoreCase("STAFF")
                || textChannel.getParent().getName().equalsIgnoreCase("TEAMS")
                || textChannel.getParent().getName().equalsIgnoreCase("LOGS"));
         */
    }

}






