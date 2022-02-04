package net.hybrid.discord.commands;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.moderation.LogDate;
import net.hybrid.discord.moderation.ReportLogger;
import net.hybrid.discord.utils.Utils;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class ReportCommand extends BotCommand {

    public ReportCommand() {
        super("report");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {

        if (args.length == 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setAuthor("Missing arguments! Valid usage: !report <user> <reason>");
            channel.sendMessage(embed.build()).queue();
            return;
        }

        if (args.length == 2) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setAuthor("You need to add a reason for the report! Valid usage: !report <user> <reason>");
            channel.sendMessage(embed.build()).queue();
            return;
        }

        boolean foundMember = false;
        Member against = null;

        if (message.getMentionedMembers().size() != 0) {
            against = message.getMentionedMembers().get(0);
            foundMember = true;

        } else {
            for (Member target : DiscordApplication.getInstance().getDiscordServer().getMembers()) {
                if (target.getEffectiveName().equalsIgnoreCase(args[1])) {
                    against = target;
                    foundMember = true;
                    break;
                }
            }

            if (against == null) {
                try {
                    against = Utils.getUserFromMember(Utils.getUserFromID(args[1]));
                    foundMember = true;
                } catch (Exception ignored) {}
            }
        }

        if (!foundMember) {
            EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                    .setAuthor("The user '" + args[1] + "' could not be found! Try to add them as a ping or use their discord id instead for better accuracy!");
            channel.sendMessage(embed.build()).queue();
            return;
        }

        StringBuilder reason = new StringBuilder();
        int number = 0;
        for (String s : args) {
            if (number >= 2) {
                reason.append(s).append(" ");
            }

            number++;
        }

        ReportLogger.logReportBackend(member.getIdLong(), against.getIdLong(), reason.toString().trim(), channel.getId(),
                getLogDate());
        ReportLogger.logReportFrontend(against, member, reason.toString().trim(), channel, getLogDate());

        message.delete().reason("Message deleted from !report command").queue();

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN).setTitle("Report Complete");
        embed.appendDescription("Thank you for reporting **" + against.getEffectiveName() + "**! This report has been logged and will be review by our staff team.");
        channel.sendMessage(embed.build()).queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
    }

    private LogDate getLogDate() {
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        LocalDateTime now = LocalDateTime.now();

        return new LogDate(now.format(year), now.format(month), now.format(day));
    }

}
















