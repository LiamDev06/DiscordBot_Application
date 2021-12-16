package net.hybrid.discord.managers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utility.DiscordRole;
import net.hybrid.discord.utility.Utils;

import javax.annotation.Nonnull;

public class RolesReaction extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Permission permission = Permission.ADMINISTRATOR;
        if (event.getMember() == null) return;

        if (event.getMember().hasPermission(permission)
                && event.getMessage().getContentRaw().equalsIgnoreCase("!reactionrolesadd")) {
            event.getMessage().delete().queue();

            event.getChannel().sendMessage(
                    "**__Reaction Roles__**" +
                            "\n" +
                            "React with the appropriate emote to subscribe to a ping notification when " +
                            "certain network events happen such as updates or tournaments.\n\n" +
                            "> :bookmark_tabs: - **Frequent Updates**\n" +
                            "> :loudspeaker: - **Events**\n" +
                            "> :bell: - **Survival Updates**\n" +
                            "> :crossed_swords: - **Free For All Updates**\n\n" +
                            "***Note:** Clicking the reaction emote once again will remove the ping role.*"
            ).queue(message -> {
                message.addReaction("U+1F4D1").queue();
                message.addReaction("U+1F4E2").queue();
                message.addReaction("U+1F514").queue();
                message.addReaction("U+2694").queue();
            });
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        final long reactionRolesChannel = 882764130862366770L;

        if (event.getChannel().getIdLong() == reactionRolesChannel && !event.getUser().isBot()){
            final String reaction = event.getReactionEmote().getAsCodepoints();
            final Role updates = DiscordRole.PING_UPDATES;
            final Role events = DiscordRole.PING_EVENTS;
            final Role survivalUpdates = DiscordRole.PING_SURVIVAL_UPDATES;
            final Role ffaUpdates = DiscordRole.PING_FFA_UPDATES;

            if (reaction.equalsIgnoreCase("U+1F4D1") && updates != null) {
                if (Utils.hasRole(event.getMember(), updates)) {
                    event.getGuild().removeRoleFromMember(event.getUserId(), updates)
                            .reason("Role removed due to reaction role un-subscription")
                            .queue();
                } else {
                    event.getGuild().addRoleToMember(event.getUserId(), updates)
                            .reason("Role added due to reaction role subscription")
                            .queue();
                }
            }

            if (reaction.equalsIgnoreCase("U+1F4E2") && events != null) {
                if (Utils.hasRole(event.getMember(), events)) {
                    event.getGuild().removeRoleFromMember(event.getUserId(), events)
                            .reason("Role removed due to reaction role un-subscription")
                            .queue();
                } else {
                    event.getGuild().addRoleToMember(event.getUserId(), events)
                            .reason("Role added due to reaction role subscription")
                            .queue();
                }
            }

            if (reaction.equalsIgnoreCase("U+1F514") && survivalUpdates != null) {
                if (Utils.hasRole(event.getMember(), survivalUpdates)) {
                    event.getGuild().removeRoleFromMember(event.getUserId(), survivalUpdates)
                            .reason("Role removed due to reaction role un-subscription")
                            .queue();
                } else {
                    event.getGuild().addRoleToMember(event.getUserId(), survivalUpdates)
                            .reason("Role added due to reaction role subscription")
                            .queue();
                }
            }

            if (reaction.equalsIgnoreCase("U+2694") && ffaUpdates != null) {
                if (Utils.hasRole(event.getMember(), ffaUpdates)) {
                    event.getGuild().removeRoleFromMember(event.getUserId(), ffaUpdates)
                            .reason("Role removed due to reaction role un-subscription")
                            .queue();
                } else {
                    event.getGuild().addRoleToMember(event.getUserId(), ffaUpdates)
                            .reason("Role added due to reaction role subscription")
                            .queue();
                }
            }

            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }
}
