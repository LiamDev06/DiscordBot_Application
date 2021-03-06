package net.hybrid.discord.commands.staff;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.filters.ChatActionEvents;
import net.hybrid.discord.utils.Utils;

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

            for (Message target : messages) {
                ChatActionEvents.shouldNotSendDeleted.add(target.getId());
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setAuthor("Command Processed! Deleting messages...");

            int deletedAmount = messages.size() - 1;

            message.delete().reason(deletedAmount + "message(s) deleted by " + member.getEffectiveName() + " via !clear").queue();
            channel.sendMessage(embed.build()).queue(message1 -> message1.delete().queueAfter(3, TimeUnit.SECONDS));
            channel.deleteMessages(messages).queueAfter(1,TimeUnit.SECONDS);

            EmbedBuilder logEmbed = new EmbedBuilder();
            logEmbed.setColor(Color.CYAN);
            logEmbed.setTitle("Messages Cleared");
            logEmbed.appendDescription("Someone cleared chat messages in a channel!\n\n");
            logEmbed.appendDescription("**Who:** <@" + member.getId() + ">");
            logEmbed.appendDescription("\n**Channel:** <#" + channel.getId() + ">");
            logEmbed.appendDescription("\n**Message Amount:** " + deletedAmount);
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














