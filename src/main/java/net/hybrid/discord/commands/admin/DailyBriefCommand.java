package net.hybrid.discord.commands.admin;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utility.DiscordRole;
import net.hybrid.discord.utility.Utils;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class DailyBriefCommand extends BotCommand {

    public DailyBriefCommand() {
        super("dailybrief");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        if (!Utils.hasRole(member, DiscordRole.OWNER)) return;
        message.delete().queue();

        if (args.length == 1) {
            channel.sendMessage(":stop_sign: Missing arguments! Please include a brief message, use !dailybrief <message...>")
                    .queue(message1 -> message1.delete().queueAfter(2, TimeUnit.SECONDS));
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.appendDescription("Daily Morning brief sent by <@" + member.getIdLong() + "> sent on " + getDate() + ", " + getTime() + " GMT.\n\n\n**Briefing Message:**\n");
        embed.setAuthor(member.getEffectiveName() + "#" + member.getUser().getDiscriminator() + "", member.getUser().getEffectiveAvatarUrl(), member.getUser().getEffectiveAvatarUrl());

        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String s : args) {
            if (count >= 1) {
                builder.append(s).append(" ");
            }

            count++;
        }

        embed.appendDescription(builder.toString().trim());

        DiscordApplication.getInstance().getDiscordServer().getTextChannelById(909891729778438144L).sendMessage(embed.build()).queue();
    }

    private static String getDate() {
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        LocalDateTime now = LocalDateTime.now();

        // MM dd yyyy
        return month.format(now) + "/" + day.format(now) + "/" + year.format(now).substring(2);
    }

    private String getTime() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return timeFormat.format(now);
    }

}










