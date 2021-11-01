package net.hybrid.discord.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public class ReportReactionListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (!event.getChannel().getName().equalsIgnoreCase("reports")) return;
        if (event.getUser().isBot()) return;

        // GREEN CIRCLE
        if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+1F7E2")) {
            event.getReaction().removeReaction(event.getUser()).queue();

            MessageHistory history = new MessageHistory(event.getChannel());
            List<Message> messages = history.retrievePast(100).complete();

            for (Message message : messages) {
                if (message.getIdLong() == event.getMessageIdLong()) {
                    EmbedBuilder replyEmbed = new EmbedBuilder();
                    replyEmbed.setColor(Color.GREEN);
                    replyEmbed.appendDescription("This report has been resolved by <@" + event.getUserId() + ">.\n");
                    replyEmbed.appendDescription("A punishment **was not** issued.");

                    MessageEmbed messageEmbed = message.getEmbeds().get(0);

                    EmbedBuilder editEmbed = new EmbedBuilder();
                    editEmbed.setColor(Color.GREEN);
                    editEmbed.setTitle("~~Discord User Report~~ [RESOLVED]");
                    editEmbed.setAuthor(messageEmbed.getAuthor().getName(), messageEmbed.getAuthor().getIconUrl(), messageEmbed.getAuthor().getIconUrl());
                    editEmbed.appendDescription("This report has been resolved by a staff member. A punishment was not executed.\n\n");
                    editEmbed.appendDescription("**Status:** Resolved\n");
                    editEmbed.appendDescription("**Punishment Issued:** None");
                    editEmbed.appendDescription(messageEmbed.getDescription()
                            .replace("A new report has been issued by a player!", "")
                    .replace("This log file contains information regarding the report.\n\n", "")
                    .replace("\n**Punishment Issued:** N/A", "")
                    .replace("**Status:** Open", ""));

                    message.reply(replyEmbed.build()).queue();
                    message.editMessage(editEmbed.build()).queue();

                    message.removeReaction("U+1F7E2").queue();
                    message.removeReaction("U+1F7E8").queue();
                    break;
                }
            }

        }

        // YELLOW SQUARE
        if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+1F7E8")) {
            event.getReaction().removeReaction(event.getUser()).queue();

            MessageHistory history = new MessageHistory(event.getChannel());
            List<Message> messages = history.retrievePast(100).complete();

            for (Message message : messages) {
                if (message.getIdLong() == event.getMessageIdLong()) {
                    EmbedBuilder replyEmbed = new EmbedBuilder();
                    replyEmbed.setColor(Color.MAGENTA);
                    replyEmbed.appendDescription("This report has been resolved by <@" + event.getUserId() + ">.\n");
                    replyEmbed.appendDescription("A punishment **has been** issued.");

                    MessageEmbed messageEmbed = message.getEmbeds().get(0);

                    EmbedBuilder editEmbed = new EmbedBuilder();
                    editEmbed.setColor(Color.MAGENTA);
                    editEmbed.setTitle("~~Discord User Report~~ [RESOLVED]");
                    editEmbed.setAuthor(messageEmbed.getAuthor().getName(), messageEmbed.getAuthor().getIconUrl(), messageEmbed.getAuthor().getIconUrl());
                    editEmbed.appendDescription("This report has been resolved by a staff member. A punishment was also issued with the report.\n\n");
                    editEmbed.appendDescription("**Status:** Resolved\n");
                    editEmbed.appendDescription("**Punishment Issued:** Yes");
                    editEmbed.appendDescription(messageEmbed.getDescription()
                            .replace("A new report has been issued by a player!", "")
                            .replace("This log file contains information regarding the report.\n\n", "")
                            .replace("\n**Punishment Issued:** N/A", "")
                            .replace("**Status:** Open", ""));

                    message.reply(replyEmbed.build()).queue();
                    message.editMessage(editEmbed.build()).queue();

                    message.removeReaction("U+1F7E2").queue();
                    message.removeReaction("U+1F7E8").queue();
                    break;
                }
            }
        }

    }
}















