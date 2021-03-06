package net.hybrid.discord.commands.admin;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ResetChatCommand extends BotCommand {

    public ResetChatCommand(){
        super("resetchannel");
    }

    @Override
    @Deprecated
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        if (member == null) return;
        if (!Utils.hasRole(member, DiscordRole.OWNER)
                && !Utils.hasRole(member, DiscordRole.DISCORD_MANAGER)
                && !Utils.hasRole(member, DiscordRole.ADMIN)) return;

        if (Utils.isPermanentChannel(channel)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.appendDescription("This is a permanent channel and therefore cannot be reset!");
            channel.sendMessage(embed.build()).queue();
            return;
        }

        channel.sendMessage(":desktop: Resetting...").queue();
        channel.createCopy().setPosition(channel.getPosition()).queueAfter(2, TimeUnit.SECONDS);
        channel.delete().reason("Channel deletion requested by " + member.getEffectiveName() + " via !resetchannel").queueAfter(2, TimeUnit.SECONDS);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle("Channel Reset");
        embed.appendDescription("A channel got reset by a staff member!\n\n");
        embed.appendDescription("**Who:** <@" + member.getId() + ">");
        embed.appendDescription("\n**Channel:** " + channel.getName());
        Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();
    }
}







