package net.hybrid.discord.filters;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utility.Utils;

import javax.annotation.Nonnull;
import java.awt.*;

public class MessageLengthFilter extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (Utils.isStaff(event.getMember())) return;
        if (Utils.isStaffChannel(event.getChannel())) return;
        if (!Utils.hasChatFilter(event.getChannel())) return;

        int charAmount = event.getMessage().getContentRaw().length();
        int wordAmount = event.getMessage().getContentRaw().split(" ").length;

        if (charAmount >= 900) {
            ChatActionEvents.shouldNotSendDeleted.add(event.getMessageId());
            event.getMessage().delete().reason("Exceeded allowed message length").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("Message Length (Spam Protection)");
            embed.appendDescription("A message exceeded the allowed message length!\n\n");
            embed.appendDescription("**User:** <@" + event.getMember().getIdLong() + ">\n");
            embed.appendDescription("**Characters:** " + charAmount);
            embed.appendDescription("\n**Words:** " + wordAmount + "\n\n");
            embed.appendDescription("**Deleted Message**:\n");

            try {
                embed.appendDescription(event.getMessage().getContentDisplay());
            } catch (Exception exception) {
                embed.appendDescription("[BOT DEBUG] MESSAGE WAS TOO LONG FOR ALLOWED EMBED MESSAGE!");
            }

            Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();
        }

    }
}















