package net.hybrid.discord.managers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utility.DiscordRole;
import net.hybrid.discord.utility.Utils;

import javax.annotation.Nonnull;
import java.awt.*;

public class JoinLeaveManager extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
        int memberCount = event.getGuild().getMemberCount() - 2;
        assert DiscordRole.MEMBER != null;

        event.getGuild().addRoleToMember(event.getUser().getId(), DiscordRole.MEMBER).queue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle("User Joined");
        embed.appendDescription("**Who:** @" + event.getUser().getName() + "#" + event.getUser().getDiscriminator());
        embed.appendDescription("\nThe main discord now has **" + memberCount + "** members.");

        Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();

        EmbedBuilder join = new EmbedBuilder();
        join.setColor(Color.GREEN);
        join.setTitle(":wave: Welcome to Hybrid");
        join.appendDescription("Welcome to the official Hybrid discord!\n");
        join.appendDescription("This is the community discord for the Hybrid Minecraft server, and we strive to bring the community together by " +
                "hosting events and other fun activities!\n\n");
        join.appendDescription(":green_circle: **Read the rules at** <#" + 880208064211484691L + "> \n");
        join.appendDescription(":newspaper: **View the latest announcements at** <#" + 880208195845509190L + ">");

         event.getGuild().getTextChannelById(880208064513445918L)
                 .sendMessage("<@" + event.getUser().getId() + ">").queue();
        event.getGuild().getTextChannelById(880208064513445918L)
                .sendMessage(join.build()).queue();
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event){
        int memberCount = event.getGuild().getMemberCount() - 2;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle("User Left");
        embed.appendDescription("**Who:** @" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "\n");
        embed.appendDescription("The main discord now has **" + memberCount + "** members.");

        Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();
    }
}
