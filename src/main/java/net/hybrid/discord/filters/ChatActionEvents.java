package net.hybrid.discord.filters;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utility.ChatAction;
import net.hybrid.discord.utility.Utils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;

public class ChatActionEvents extends ListenerAdapter {

    //                 Msg ID   Last MSG
    private final HashMap<String, String> messages = new HashMap<>();

    //                   Msg ID   Member
    private final HashMap<String, Member> messageMember = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (Utils.isStaff(event.getMember())) return;
        if (Utils.isStaffChannel(event.getChannel())) return;
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().isSystem()) return;

        messages.remove(event.getMessageId());
        messages.put(event.getMessageId(), event.getMessage().getContentRaw());

        messageMember.remove(event.getMessageId());
        messageMember.put(event.getMessageId(), event.getMember());
    }

    @Override
    public void onGuildMessageDelete(@Nonnull GuildMessageDeleteEvent event) {
        if (!messageMember.containsKey(event.getMessageId())) return;

        try {
            Member member = messageMember.get(event.getMessageId());
            if (member.getUser().isBot()) return;
            if (member.getUser().isSystem()) return;
            if (Utils.isStaff(member)) return;
            if (Utils.isStaffChannel(event.getChannel())) return;

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setAuthor(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl(), member.getUser().getEffectiveAvatarUrl());
            embed.addField("Action", ChatAction.DELETION.name(), true);
            embed.addField("Author", "<@" + member.getId() + ">", true);
            embed.addField("Message ID", event.getMessageId(), true);
            embed.addField("Channel", "<#" + event.getChannel().getId() + ">", true);
            embed.addBlankField(true);
            embed.addBlankField(true);
            embed.addField("Message", messages.get(event.getMessageId()), true);

            Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();

        } catch (Exception ignored) {}
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        if (event.getMember() == null) return;
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().isSystem()) return;
        if (Utils.isStaff(event.getMember())) return;
        if (Utils.isStaffChannel(event.getChannel())) return;

        String before = messages.getOrDefault(event.getMessageId(),
                "COULD NOT BE FETCHED");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getUser().getEffectiveAvatarUrl());
        embed.addField("Action", ChatAction.EDIT.name(), true);
        embed.addField("Author", "<@" + event.getMember().getId() + ">", true);
        embed.addField("Message ID", event.getMessageId(), true);
        embed.addField("Channel", "<#" + event.getChannel().getId() + ">", true);
        embed.addBlankField(true);
        embed.addBlankField(true);
        embed.addField("Before", before, true);
        embed.addField("After", event.getMessage().getContentDisplay(), true);

        Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();

        messages.remove(event.getMessageId());
        messages.put(event.getMessageId(), event.getMessage().getContentRaw());

        messageMember.remove(event.getMessageId());
        messageMember.put(event.getMessageId(), event.getMember());
    }

}








