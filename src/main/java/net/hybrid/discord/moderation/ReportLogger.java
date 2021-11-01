package net.hybrid.discord.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utility.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ReportLogger {

    private static final File file = new File(DiscordApplication.getInstance().getDataFolder(), "reports.yml");
    private static final FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static void logReportBackend(long issuerId, long againstId, String reason, String channelIssuedIn, LogDate logDate) {
        int globalId = config.getInt("reportsGlobalId");
        String path = "report-" + (globalId + 1) + ".";

        config.set(path + "issuerId", issuerId);
        config.set(path + "againstId", againstId);
        config.set(path + "reason", reason);
        config.set(path + "channelIssuedIn", channelIssuedIn);
        config.set(path + "dateIssued.year", logDate.getYear());
        config.set(path + "dateIssued.month", logDate.getMonth());
        config.set(path + "dateIssued.day", logDate.getDay());

        config.set("reportsGlobalId", config.getInt("reportsGlobalId") + 1);

        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void logReportFrontend(Member against, Member issuer, String reason, TextChannel channelIssuedIn, LogDate logDate) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle("Discord User Report");
        embed.setAuthor(issuer.getEffectiveName(), issuer.getUser().getEffectiveAvatarUrl(), issuer.getUser().getEffectiveAvatarUrl());
        embed.appendDescription("A new report has been issued by a player! This log file contains information regarding the report.\n\n");
        embed.appendDescription("**Status:** Open");
        embed.appendDescription("\n**Punishment Issued:** N/A");
        embed.appendDescription("\n\n**Report Issued By:** <@" + issuer.getId() + ">");
        embed.appendDescription("\n**Reported User (Rule Breaker):** <@" + against.getId() + ">");
        embed.appendDescription("\n\n");
        embed.appendDescription("**Reason for Report:**\n");
        embed.appendDescription(reason);
        embed.appendDescription("\n\n**Channel Report Issued In:** <#" + channelIssuedIn.getId() + ">");
        embed.appendDescription("\n**Date Report Issued:** " + logDate.getMonth() + "/" + logDate.getDay() + "/" + logDate.getYear());
        embed.setFooter("Report ID: " + config.getInt("reportsGlobalId"));

        Utils.getUserReportsChannel().sendMessage(embed.build()).queue(message -> {
            message.addReaction("U+1F7E2").queue();
            message.addReaction("U+1F7E8").queue();
        });
    }

}













