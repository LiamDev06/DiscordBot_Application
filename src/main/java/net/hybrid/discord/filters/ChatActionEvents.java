package net.hybrid.discord.filters;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utility.ChatAction;
import net.hybrid.discord.utility.Utils;

import javax.annotation.Nonnull;
import java.awt.*;

public class ChatActionEvents extends ListenerAdapter {

    @Override
    public void onGuildMessageDelete(@Nonnull GuildMessageDeleteEvent event) {

    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        if (event.getMember() == null) return;
        if (Utils.isStaff(event.getMember())) return;
        if (Utils.isStaffChannel(event.getChannel())) return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getUser().getEffectiveAvatarUrl());
        embed.addField("Action", ChatAction.EDIT.name(), true);
        embed.addField("Author", "<@" + event.getMember().getId() + ">", true);
        embed.addField("Message ID", event.getMessageId(), true);
        embed.addField("Channel", "<#" + event.getChannel().getId() + ">", true);
        embed.addBlankField(true);
        embed.addBlankField(true);
        embed.addField("Before", "BEFORE", true);
        embed.addField("After", event.getMessage().getContentDisplay(), true);

        Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();
    }

}








