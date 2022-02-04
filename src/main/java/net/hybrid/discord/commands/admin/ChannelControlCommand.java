package net.hybrid.discord.commands.admin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.EnumSet;

public class ChannelControlCommand extends ListenerAdapter {


    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equalsIgnoreCase("channelcontrol")) return;
        TextChannel channel = event.getTextChannel();

        if (event.getSubcommandName().equalsIgnoreCase("fetch")) {
            if (event.getOptions().size() == 0) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Channel Options");
                embed.setColor(Color.YELLOW);
                embed.appendDescription("Fetching channel options for channel <#" + channel.getId() + ">.\n\n");

                if (Utils.hasChatFilter(channel)) {
                    embed.appendDescription("**➤ Chat Filter:** Enabled\n");
                } else {
                    embed.appendDescription("**➤ Chat Filter:** Disabled\n");
                }

                if (Utils.hasChatLogging(channel)) {
                    embed.appendDescription("**➤ Chat Logging:** Enabled\n");
                } else {
                    embed.appendDescription("**➤ Chat Logging:** Disabled\n");
                }

                if (Utils.hasAutoSlowMode(channel)) {
                    embed.appendDescription("**➤ Auto Slow Mode:** Enabled\n");
                } else {
                    embed.appendDescription("**➤ Auto Slow Mode:** Disabled\n");
                }

                if (Utils.isStaffChannel(channel)) {
                    embed.appendDescription("**➤ Is Staff Channel:** True\n");
                } else {
                    embed.appendDescription("**➤ Is Staff Channel:** False\n");
                }

                if (Utils.isPermanentChannel(channel)) {
                    embed.appendDescription("**➤ Is Permanent Channel:** True\n");
                } else {
                    embed.appendDescription("**➤ Is Permanent Channel:** False\n");
                }

                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
                return;
            }

            if (!(event.getOptions().get(0).getAsGuildChannel() instanceof TextChannel)) {
                event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                .setColor(Color.RED).appendDescription("This can only be used with a text channel!").build()).queue();
                return;
            }

            TextChannel target = (TextChannel) event.getOptions().get(0).getAsGuildChannel();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Channel Options");
            embed.setColor(Color.YELLOW);
            embed.appendDescription("Fetching channel options for channel <#" + target.getId() + ">.\n\n");

            if (Utils.hasChatFilter(target)) {
                embed.appendDescription("**➤ Chat Filter:** Enabled\n");
            } else {
                embed.appendDescription("**➤ Chat Filter:** Disabled\n");
            }

            if (Utils.hasChatLogging(target)) {
                embed.appendDescription("**➤ Chat Logging:** Enabled\n");
            } else {
                embed.appendDescription("**➤ Chat Logging:** Disabled\n");
            }

            if (Utils.hasAutoSlowMode(target)) {
                embed.appendDescription("**➤ Auto Slow Mode:** Enabled\n");
            } else {
                embed.appendDescription("**➤ Auto Slow Mode:** Disabled\n");
            }

            if (Utils.isStaffChannel(target)) {
                embed.appendDescription("**➤ Is Staff Channel:** True\n");
            } else {
                embed.appendDescription("**➤ Is Staff Channel:** False\n");
            }

            if (Utils.isPermanentChannel(target)) {
                embed.appendDescription("**➤ Is Permanent Channel:** True\n");
            } else {
                embed.appendDescription("**➤ Is Permanent Channel:** False\n");
            }

            event.deferReply(false).addEmbeds(embed.build()).queue();
        }

        if (event.getSubcommandName().equalsIgnoreCase("chatfilter")) {
            boolean value = event.getOptions().get(0).getAsBoolean();
            TextChannel target;

            try {
                if (!(event.getOptions().get(1).getAsGuildChannel() instanceof TextChannel)) {
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("This can only be used with a text channel!").build()).queue();
                    return;
                }

                target = (TextChannel) event.getOptions().get(1).getAsGuildChannel();
            } catch (IndexOutOfBoundsException ignored) {
                target = event.getTextChannel();
            }

            if (value) {
                if (Utils.hasChatFilter(target)) {
                    // ALREADY HAS FILTER
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> already has chat filter enabled!").build()).queue();
                    return;
                }

                // TURN ON FILTER
                target.getManager().removePermissionOverride(DiscordRole.BOT_NO_CHAT_FILTER)
                        .reason("Chat Filter Enabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                        .appendDescription("Chat Filter has been enabled for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            } else {
                if (!Utils.hasChatFilter(target)) {
                    // DOES NOT HAVE FILTER
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> does not have chat filter enabled!").build()).queue();
                    return;
                }

                // TURN OFF FILTER
                target.getManager().putPermissionOverride(DiscordRole.BOT_NO_CHAT_FILTER, EnumSet.of(Permission.VIEW_CHANNEL), null)
                        .reason("Chat Filter Disabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                        .appendDescription("Chat Filter has been disabled for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            }
        }

        if (event.getSubcommandName().equalsIgnoreCase("chatlogging")) {
            boolean value = event.getOptions().get(0).getAsBoolean();
            TextChannel target;

            try {
                if (!(event.getOptions().get(1).getAsGuildChannel() instanceof TextChannel)) {
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("This can only be used with a text channel!").build()).queue();
                    return;
                }

                target = (TextChannel) event.getOptions().get(1).getAsGuildChannel();
            } catch (IndexOutOfBoundsException ignored) {
                target = event.getTextChannel();
            }

            if (value) {
                if (Utils.hasChatLogging(target)) {
                    // ALREADY HAS LOGGING
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> already has chat logging enabled!").build()).queue();
                    return;
                }

                // TURN ON LOGGING
                target.getManager().removePermissionOverride(DiscordRole.BOT_NO_CHAT_LOGGING)
                        .reason("Chat Logging Enabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                        .appendDescription("Chat Logging has been enabled for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            } else {
                if (!Utils.hasChatLogging(target)) {
                    // DOES NOT HAVE LOGGING
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> does not have chat logging enabled!").build()).queue();
                    return;
                }

                // TURN OFF LOGGING
                target.getManager().putPermissionOverride(DiscordRole.BOT_NO_CHAT_LOGGING, EnumSet.of(Permission.VIEW_CHANNEL), null)
                        .reason("Chat Logging Disabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                        .appendDescription("Chat Logging has been disabled for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            }
        }

        if (event.getSubcommandName().equalsIgnoreCase("autoslowmode")) {
            boolean value = event.getOptions().get(0).getAsBoolean();
            TextChannel target;

            try {
                if (!(event.getOptions().get(1).getAsGuildChannel() instanceof TextChannel)) {
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("This can only be used with a text channel!").build()).queue();
                    return;
                }

                target = (TextChannel) event.getOptions().get(1).getAsGuildChannel();
            } catch (IndexOutOfBoundsException ignored) {
                target = event.getTextChannel();
            }

            if (value) {
                if (Utils.hasAutoSlowMode(target)) {
                    // ALREADY HAS AUTO SLOW MODE
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> already has auto slow mode control enabled!").build()).queue();
                    return;
                }

                // TURN ON AUTO SLOW MODE
                target.getManager().putPermissionOverride(DiscordRole.BOT_AUTO_SLOW_MODE, EnumSet.of(Permission.VIEW_CHANNEL), null)
                        .reason("Auto Slow Mode control Enabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                        .appendDescription("Auto Slow Mode control has been enabled for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            } else {
                if (!Utils.hasAutoSlowMode(target)) {
                    // DOES NOT HAVE AUTO SLOW MODE
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> does not have auto slow mode control enabled!").build()).queue();
                    return;
                }

                // TURN OFF AUTO SLOW MODE
                target.getManager().removePermissionOverride(DiscordRole.BOT_AUTO_SLOW_MODE)
                        .reason("Auto Slow Mode control Disabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                        .appendDescription("Auto Slow Mode control has been disabled for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            }
        }

        if (event.getSubcommandName().equalsIgnoreCase("staffchannel")) {
            boolean value = event.getOptions().get(0).getAsBoolean();
            TextChannel target;

            try {
                if (!(event.getOptions().get(1).getAsGuildChannel() instanceof TextChannel)) {
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("This can only be used with a text channel!").build()).queue();
                    return;
                }

                target = (TextChannel) event.getOptions().get(1).getAsGuildChannel();
            } catch (IndexOutOfBoundsException ignored) {
                target = event.getTextChannel();
            }

            if (value) {
                if (Utils.isStaffChannel(target)) {
                    // ALREADY IS STAFF CHANNEL
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> is already marked as a staff channel!").build()).queue();
                    return;
                }

                // TURN ON IS STAFF CHANNEL
                target.getManager().putPermissionOverride(DiscordRole.BOT_STAFF_CHANNEL, EnumSet.of(Permission.VIEW_CHANNEL), null)
                        .reason("Is Staff Channel Enabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                        .appendDescription("Staff Channel has been marked for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            } else {
                if (!Utils.isStaffChannel(target)) {
                    // DOES NOT IS STAFF CHANNEL
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> is not marked as a staff channel!").build()).queue();
                    return;
                }

                // TURN OFF IS STAFF CHANNEL
                target.getManager().removePermissionOverride(DiscordRole.BOT_STAFF_CHANNEL)
                        .reason("Is Staff Channel Disabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                        .appendDescription("Staff Channel has been unmarked for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            }
        }

        if (event.getSubcommandName().equalsIgnoreCase("permanent")) {
            boolean value = event.getOptions().get(0).getAsBoolean();
            TextChannel target;

            try {
                if (!(event.getOptions().get(1).getAsGuildChannel() instanceof TextChannel)) {
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("This can only be used with a text channel!").build()).queue();
                    return;
                }

                target = (TextChannel) event.getOptions().get(1).getAsGuildChannel();
            } catch (IndexOutOfBoundsException ignored) {
                target = event.getTextChannel();
            }

            if (value) {
                if (Utils.isPermanentChannel(target)) {
                    // ALREADY IS PERMANENT CHANNEL
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> is already marked as a permanent channel!").build()).queue();
                    return;
                }

                // TURN ON IS PERMANENT CHANNEL
                target.getManager().putPermissionOverride(DiscordRole.BOT_PERMANENT_CHANNEL, EnumSet.of(Permission.VIEW_CHANNEL), null)
                        .reason("Is Permanent Channel Enabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                        .appendDescription("Permanent channel has been marked for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            } else {
                if (!Utils.isPermanentChannel(target)) {
                    // DOES NOT IS PERMANENT CHANNEL
                    event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED).appendDescription("The channel <#" + target.getId() + "> is not marked as a permanent channel!").build()).queue();
                    return;
                }

                // TURN OFF IS PERMANENT CHANNEL
                target.getManager().removePermissionOverride(DiscordRole.BOT_PERMANENT_CHANNEL)
                        .reason("Is Permanent Channel Disabled")
                        .queue();

                EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                        .appendDescription("Permanent channel has been unmarked for channel <#" + target.getId() + ">.");
                event.getInteraction().deferReply(false).addEmbeds(embed.build()).queue();
            }
        }

    }
}













