package net.hybrid.discord.managers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
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
                    "**Reaction Roles Available**" +
                            "\n" +
                            "React with the appropriate reaction to get a ping notification when " +
                            "certain events happen such as updates or tournaments.\n\n" +
                            ":bookmark_tabs: for **Frequent Updates** ping!\n" +
                            ":loudspeaker: for **Event** ping!\n" +
                            ":bell: for **Survival Updates** ping!"
            ).queue(message -> {
                message.addReaction("U+1F4D1").queue();
                message.addReaction("U+1F4E2").queue();
                message.addReaction("U+1F514").queue();
            });
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        long verificationChannelID = 882764130862366770L;

        if (event.getChannel().getIdLong() == verificationChannelID){
            if (!event.getUser().isBot()) {
                String reaction = event.getReactionEmote().getAsCodepoints();
                Role frequentUpdates = DiscordRole.PING_FREQUENT_UPDATES;
                Role events = DiscordRole.PING_EVENTS;
                Role survivalUpdates = DiscordRole.PING_SURVIVAL_UPDATES;

                if (reaction.equalsIgnoreCase("U+1F4D1")) {
                    if (frequentUpdates == null) return;

                    event.getGuild().addRoleToMember(event.getUserId(), frequentUpdates).queue();
                }

                if (reaction.equalsIgnoreCase("U+1F4E2")) {
                    if (events == null) return;

                    event.getGuild().addRoleToMember(event.getUserId(), events).queue();
                }

                if (reaction.equalsIgnoreCase("U+1F514")) {
                    if (survivalUpdates == null) return;

                    event.getGuild().addRoleToMember(event.getUserId(), survivalUpdates).queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        long verificationChannelID = 882764130862366770L;

        if (event.getChannel().getIdLong() == verificationChannelID && event.getMember() != null) {
            String reaction = event.getReactionEmote().getAsCodepoints();
            Role frequentUpdates = DiscordRole.PING_FREQUENT_UPDATES;
            Role events = DiscordRole.PING_EVENTS;
            Role survivalUpdates = DiscordRole.PING_SURVIVAL_UPDATES;

            if (reaction.equals("U+1F4D1")) {
                if (Utils.hasRole(event.getMember(), frequentUpdates) && frequentUpdates != null) {
                    event.getGuild().removeRoleFromMember(event.getUserId(), frequentUpdates).queue();
                }
            }

            if (reaction.equals("U+1F4E2")) {
                if (Utils.hasRole(event.getMember(), events) && events != null) {
                    event.getGuild().removeRoleFromMember(event.getUserId(), events).queue();
                }
            }

            if (reaction.equals("U+1F514")) {
                if (Utils.hasRole(event.getMember(), survivalUpdates) && survivalUpdates != null) {
                    event.getGuild().removeRoleFromMember(event.getUserId(), survivalUpdates).queue();
                }
            }
        }
    }
}
