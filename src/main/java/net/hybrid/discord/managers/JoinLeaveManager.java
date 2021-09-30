package net.hybrid.discord.managers;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utility.DiscordRole;

import javax.annotation.Nonnull;

public class JoinLeaveManager extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
        assert DiscordRole.VERIFIED != null;
        event.getGuild().addRoleToMember(event.getUser().getId(), DiscordRole.VERIFIED).queue();
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event){

    }

}
