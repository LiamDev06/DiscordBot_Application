package net.hybrid.discord.managers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;

import javax.annotation.Nonnull;
import java.awt.*;

public class RolesReaction extends ListenerAdapter {

    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equalsIgnoreCase("addreactionrolesembed")) return;

        TextChannel target = event.getTextChannel();

        if (event.getOptions().size() != 0) {
            GuildChannel guildChannel = event.getOptions().get(0).getAsGuildChannel();

            if (guildChannel instanceof TextChannel) {
                target = (TextChannel) event.getOptions().get(0).getAsGuildChannel();
            } else {
                event.deferReply(true).setContent("The specified channel must be a text channel!").queue();
                return;
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("Reaction Roles")
                .appendDescription("Click the appropriate button to subscribe to a ping notification when certain network events happen such as updates or events.")
                .appendDescription("\n\n> :bookmark_tabs: - **Frequent Updates**\n" + "> :loudspeaker: - **Events**\n" + "> :bell: - **Survival Updates**\n" + "> :evergreen_tree: - **Project Wobble Updates**\n\n")
                .appendDescription("***Note:*** Clicking the reaction button once again will unsubscribe you and remove the ping role.");

        target.sendMessageEmbeds(embed.build()).setActionRow(Button.primary("reaction_roles_frequent_updates", Emoji.fromUnicode("U+1F4D1")),
                Button.primary("reaction_roles_events", Emoji.fromUnicode("U+1F4E2")),
                Button.primary("reaction_roles_survival_updates", Emoji.fromUnicode("U+1F514")),
                Button.primary("reaction_roles_project_wobble_updates", Emoji.fromUnicode("U+1F332"))).queue();

        event.getInteraction().reply("Reaction roles embed was sent...").queue();
    }

    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if (event.getButton() == null || event.getButton().getId() == null ||
                event.getMember() == null || event.getGuild() == null) return;

        final Role updates = DiscordRole.PING_UPDATES;
        final Role events = DiscordRole.PING_EVENTS;
        final Role survivalUpdates = DiscordRole.PING_SURVIVAL_UPDATES;
        final Role projectWobbleUpdates = DiscordRole.PING_PROJECT_WOBBLE_UPDATES;

        if (event.getButton().getId().equalsIgnoreCase("reaction_roles_frequent_updates") && updates != null) {
            if (Utils.hasRole(event.getMember(), updates)) {
                event.getGuild().removeRoleFromMember(event.getUser().getId(), updates)
                        .reason("Role removed due to reaction role un-subscription")
                        .queue();

                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder().setColor(Color.YELLOW).appendDescription("You successfully unsubscribed from **Frequent Updates** notifications!").build()).queue();
            } else {
                event.getGuild().addRoleToMember(event.getUser().getId(), updates)
                        .reason("Role added due to reaction role subscription")
                        .queue();

                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder().setColor(Color.GREEN).appendDescription("You successfully subscribed to **Frequent Updates** notifications!").build()).queue();
            }
        }

        if (event.getButton().getId().equalsIgnoreCase("reaction_roles_events") && events != null) {
            if (Utils.hasRole(event.getMember(), events)) {
                event.getGuild().removeRoleFromMember(event.getUser().getId(), events)
                        .reason("Role removed due to reaction role un-subscription")
                        .queue();

                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder().setColor(Color.YELLOW).appendDescription("You successfully unsubscribed from **Events** notifications!").build()).queue();
            } else {
                event.getGuild().addRoleToMember(event.getUser().getId(), events)
                        .reason("Role added due to reaction role subscription")
                        .queue();

                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder().setColor(Color.GREEN).appendDescription("You successfully subscribed to **Events** notifications!").build()).queue();
            }
        }

        if (event.getButton().getId().equalsIgnoreCase("reaction_roles_survival_updates") && survivalUpdates != null) {
            if (Utils.hasRole(event.getMember(), survivalUpdates)) {
                event.getGuild().removeRoleFromMember(event.getUser().getId(), survivalUpdates)
                        .reason("Role removed due to reaction role un-subscription")
                        .queue();

                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder().setColor(Color.YELLOW).appendDescription("You successfully unsubscribed from **Survival Updates** notifications!").build()).queue();
            } else {
                event.getGuild().addRoleToMember(event.getUser().getId(), survivalUpdates)
                        .reason("Role added due to reaction role subscription")
                        .queue();

                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder().setColor(Color.GREEN).appendDescription("You successfully subscribed to **Survival Updates** notifications!").build()).queue();
            }
        }

        if (event.getButton().getId().equalsIgnoreCase("reaction_roles_project_wobble_updates") && projectWobbleUpdates != null) {
            if (Utils.hasRole(event.getMember(), projectWobbleUpdates)) {
                event.getGuild().removeRoleFromMember(event.getUser().getId(), projectWobbleUpdates)
                        .reason("Role removed due to reaction role un-subscription")
                        .queue();

                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder().setColor(Color.YELLOW).appendDescription("You successfully unsubscribed from **Project Wobble Updates** notifications!").build()).queue();
            } else {
                event.getGuild().addRoleToMember(event.getUser().getId(), projectWobbleUpdates)
                        .reason("Role added due to reaction role subscription")
                        .queue();

                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder().setColor(Color.GREEN).appendDescription("You successfully subscribed to **Project Wobble Updates** notifications!").build()).queue();
            }
        }
    }
}
