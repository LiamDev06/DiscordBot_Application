package net.hybrid.discord.managers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;

import javax.annotation.Nonnull;
import java.awt.*;

public class JoinLeaveManager extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
        int memberCount = event.getGuild().getMemberCount() - 2;
        assert DiscordRole.UNVERIFIED != null;

        event.getGuild().addRoleToMember(event.getUser().getId(), DiscordRole.UNVERIFIED)
                .reason("User Joined").queue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle("User Joined");
        embed.appendDescription("**Who:** @" + event.getUser().getName() + "#" + event.getUser().getDiscriminator());
        embed.appendDescription("\nThe main discord now has **" + memberCount + "** members.");

        Utils.getDiscordLogsChannel().sendMessage(embed.build()).queue();
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

        //TODO: Automatically unlink the user
    }
}
