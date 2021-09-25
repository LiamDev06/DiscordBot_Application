package net.hybrid.discord.commands;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.utility.Utils;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearMessageCommand extends BotCommand {

    public ClearMessageCommand(){
        super("clear");
    }

    @Override @Deprecated
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        if (!Utils.isStaff(member)) return;
        int amount;

        if (args.length > 1){
            try {
                amount = Integer.parseInt(args[1]);
            } catch (Exception e){
                message.delete().queue();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setAuthor("Invalid Amount!");
                channel.sendMessage("<@" + member.getId() + "> - Error").queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
                channel.sendMessage(embed.build()).queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
                return;
            }

            MessageHistory history = new MessageHistory(channel);
            List<Message> messages = history.retrievePast(amount + 1).complete();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setAuthor("Command Processed! Deleting messages...");

            message.delete().queue();
            channel.sendMessage(embed.build()).queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
            channel.deleteMessages(messages).queueAfter(1,TimeUnit.SECONDS);

            EmbedBuilder logEmbed = new EmbedBuilder();
            logEmbed.setColor(Color.CYAN);
            logEmbed.setTitle("Messages Cleared");
            logEmbed.appendDescription("Someone cleared chat messages in a channel!\n\n");
            logEmbed.appendDescription("**Who:** <@" + member.getId() + ">");
            logEmbed.appendDescription("\n**Channel:** <#" + channel.getId() + ">");
            logEmbed.appendDescription("\n**Message Amount:** " + (messages.size() - 1));
            Utils.getDiscordLogsChannel().sendMessage(logEmbed.build()).queue();

        } else {
            message.delete().queue();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.YELLOW);
            embed.setAuthor("Please specify an amount to delete!");
            channel.sendMessage("<@" + member.getId() + "> - Clear Error").queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
            channel.sendMessage(embed.build()).queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
        }
    }
}














