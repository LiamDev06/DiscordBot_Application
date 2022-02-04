package net.hybrid.discord.filters;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LinkBlockerFilter extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (Utils.isStaff(event.getMember())) return;
        if (Utils.isStaffChannel(event.getChannel())) return;
        if (!Utils.hasChatFilter(event.getChannel())) return;

        String links = "";

        if (containsLink(event.getMessage().getContentRaw().trim())) {
            ChatActionEvents.shouldNotSendDeleted.add(event.getMessageId());
            event.getMessage().delete().reason("Message contained a link").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("Link Blocker (Scam Protection)");
            embed.appendDescription("A message contained a link that was not allowed!!\n\n");
            embed.appendDescription("**User:** <@" + event.getMember().getIdLong() + ">\n");
            embed.appendDescription("**Channel:** <#" + event.getChannel().getId() + ">\n");
            embed.appendDescription("**Links:** " + links + "\n");
            embed.appendDescription("**Deleted Message**:\n");

            try {
                embed.appendDescription(event.getMessage().getContentDisplay());
            } catch (Exception exception) {
                embed.appendDescription("[BOT DEBUG] MESSAGE WAS TOO LONG FOR ALLOWED EMBED MESSAGE!");
            }

            Utils.getDiscordLogsChannel().sendMessageEmbeds(embed.build()).queue();
        }

    }

    private boolean containsLink(String input) {
        return false;
    }

}














