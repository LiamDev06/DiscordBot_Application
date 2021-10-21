package net.hybrid.discord.commands;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utility.DiscordRole;
import net.hybrid.discord.utility.Utils;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ForceVerifyCommand extends BotCommand {

    public ForceVerifyCommand() {
        super("forceverify");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        Permission permission = Permission.MESSAGE_MANAGE;
        if (!member.hasPermission(permission)) return;
        message.delete().queue();

        if (args.length == 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setAuthor("Missing arguments! Use /forceverify <userid>");
            channel.sendMessage(embed.build())
                    .queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
            return;
        }

        long userId;
        try {
            userId = Long.parseLong(args[1]);
        } catch (Exception exception) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setAuthor("Invalid user id!");
            channel.sendMessage(embed.build())
                    .queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
            return;
        }

        User user = DiscordApplication.getInstance().getJda().retrieveUserById(userId).complete();
        if (user == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setAuthor("Error! This user does not exist!");
            channel.sendMessage(embed.build())
                    .queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
            return;
        }

        Guild guild = DiscordApplication.getInstance().getDiscordServer();
        Member targetMember = guild.getMember(user);
        if (targetMember == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setAuthor("Error! This user is not a part of the Hybrid discord!");
            channel.sendMessage(embed.build())
                    .queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
            return;
        }
        if (DiscordRole.UNVERIFIED == null || DiscordRole.VERIFIED == null) return;

        if (Utils.hasRole(targetMember, DiscordRole.VERIFIED)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.YELLOW);
            embed.setAuthor("This user is already verified!");
            channel.sendMessage(embed.build())
                    .queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
            return;
        }

        guild.removeRoleFromMember(targetMember.getId(), DiscordRole.UNVERIFIED).queue();
        guild.addRoleToMember(targetMember.getId(), DiscordRole.VERIFIED).queue();

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setAuthor("You force-verified " + user.getName() + "!");
        channel.sendMessage(embed.build())
                .queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
    }
}













