package net.hybrid.discord.commands.staff;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utils.Utils;

import java.awt.*;
import java.io.File;

public class FetchLogsCommand extends BotCommand {

    public FetchLogsCommand() {
        super("fetchlogs");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        if (!Utils.isStaff(member)) return;
        if (args.length == 1 || args.length == 2) {
            channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Usage:** fetchlogs <channel> <date>\n\n*Note: Date formats are year-month-date using numbers; e.g. 2022-01-01*").build()).queue();
            return;
        }

        Guild server = DiscordApplication.getInstance().getDiscordServer();
        String channelInput = args[1].trim().replace("<", "").replace(">", "").replace("#", "");
        String[] dateInput = args[2].trim().replace("-", " ").split(" "); // year-month-day
        TextChannel fetchChannel;

        if (server.getTextChannelsByName(channelInput, true).isEmpty()) {
            try {
                fetchChannel = server.getTextChannelById(channelInput);
            } catch (Exception exception) {
                channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED)
                        .appendDescription("**Invalid Channel!** A channel with that name was not found")
                        .build()).queue();
                return;
            }
        } else {
            fetchChannel = server.getTextChannelsByName(channelInput, true).get(0);
        }

        if (fetchChannel == null) {
            channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Invalid Channel!** A channel with that name was not found")
                    .build()).queue();
            return;
        }

        if (dateInput.length != 3) {
            // Invalid date format
            channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Invalid Date Format!** Usage: year-month-date")
                    .build()).queue();
            return;
        }

        if (!member.hasAccess(fetchChannel)) {
            channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Access Denied!** You do not have access to fetch logs from channel <#" + fetchChannel.getId() + ">")
                    .build()).queue();
            return;
        }

        String friendlyDate = dateInput[0] + "-" + dateInput[1] + "-" + dateInput[2];
        File file = new File(DiscordApplication.getInstance().getDataFolder() + "/logs"
                + "/#" + fetchChannel.getName(), friendlyDate + "_log.log");

        if (!file.exists()) {
            // File was not found
            channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Not Found!** The log file for " + friendlyDate + ", with channel <#" + fetchChannel.getId() + "> was not found").build()).queue();
            return;
        }

        channel.sendMessage("**Log File Found!** Sending it now...").queue();

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setTitle("Discord Log File");
        embed.setAuthor("Requested by " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator(), "https://hybridplays.com", member.getUser().getEffectiveAvatarUrl());
        embed.appendDescription("**Requested Staff:** <@" + member.getId() + ">\n" +
                "**Date:** " + friendlyDate + "\n" +
                "**Channel:** <#" + fetchChannel.getId() + ">").build();

        if (!Utils.hasChatLogging(fetchChannel)) {
            embed.appendDescription("\n\n" +
                    ":red_circle: **WARNING!** This channel currently has chat logging disabled meaning some messages might be missing from the log file!");
        }

        channel.sendMessageEmbeds(embed.build()).queue();
        channel.sendFile(file,
                fetchChannel.getName().replace("#", "") + "_"
                        + file.getName()).queue();
    }
}












