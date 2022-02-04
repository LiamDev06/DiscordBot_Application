package net.hybrid.discord.filters;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utils.Utils;

import javax.annotation.Nonnull;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatLogs extends ListenerAdapter {

    @Override @SneakyThrows
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!Utils.hasChatLogging(event.getChannel())) return;
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

        new File(DiscordApplication.getInstance().getDataFolder() + "/logs").mkdirs();
        new File(DiscordApplication.getInstance().getDataFolder() + "/logs" + "/#" + event.getChannel().getName()).mkdirs();

        File file = new File(DiscordApplication.getInstance().getDataFolder() + "/logs"
         + "/#" + event.getChannel().getName(), year + "-" + month + "-" + day + "_log.log");

        String content = "[" + event.getMessageId() + " -==- " + time + "-GMT] " + event.getMember().getEffectiveName() + "#" + event.getAuthor().getDiscriminator() + ": " + '"' + event.getMessage().getContentStripped() + '"';

        if (Utils.isMessageBlacklisted(event.getMessage().getContentRaw()) && !Utils.isStaff(event.getMember()) && !Utils.isStaffChannel(event.getChannel()) && Utils.hasChatFilter(event.getChannel())) {
            content = "(BLACKLISTED) " + content;
        }

        FileWriter writer = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(writer);

        bw.write(content);
        bw.newLine();

        bw.flush();
        bw.close();

        File[] files = new File(DiscordApplication.getInstance().getDataFolder() +
                "/logs/#" + event.getChannel().getName()).listFiles();

        /* This causes the system to break, will need to look into
        if (files.length >= 5) {
            Arrays.stream(files).findFirst().get().delete();
        }
         */
    }
}











