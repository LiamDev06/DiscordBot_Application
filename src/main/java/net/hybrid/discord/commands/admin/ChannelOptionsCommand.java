package net.hybrid.discord.commands.admin;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.utility.DiscordRole;
import net.hybrid.discord.utility.Utils;

import java.awt.*;
import java.util.EnumSet;

public class ChannelOptionsCommand extends BotCommand {

    public ChannelOptionsCommand() {
        super("channeloptions");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        if (!Utils.hasRole(member, DiscordRole.ADMIN) && !Utils.hasRole(member, DiscordRole.OWNER))
            return;

        if (args.length == 1) {
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

            channel.sendMessage(embed.build()).queue();
            return;
        }

        if (args[1].equalsIgnoreCase("filter")) {
            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("enable")) {
                    if (!Utils.hasChatFilter(channel)) {
                        channel.getManager().removePermissionOverride(DiscordRole.BOT_NO_CHAT_FILTER).queue();

                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                                .appendDescription("Chat Filter has been enabled for this channel (<#" + channel.getId() + ">).");
                        channel.sendMessage(embed.build()).queue();
                        return;
                    }

                    EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED);
                    embed.setAuthor("This channel already has the chat filter enabled!");
                    channel.sendMessage(embed.build()).queue();
                } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("disable")) {
                    if (Utils.hasChatFilter(channel)) {
                        channel.getManager().putPermissionOverride(DiscordRole.BOT_NO_CHAT_FILTER, EnumSet.of(Permission.VIEW_CHANNEL), null).queue();

                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                                .appendDescription("Chat Filter has been disabled for this channel (<#" + channel.getId() + ">).");
                        channel.sendMessage(embed.build()).queue();

                    } else {
                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED);
                        embed.setAuthor("This channel does not have the filter enabled!");
                        channel.sendMessage(embed.build()).queue();
                    }
                } else {
                    EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                            .setAuthor("Invalid value! Valid usage: !channeloptions filter [true/false]");
                    channel.sendMessage(embed.build()).queue();
                }
            } else {
                EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                        .setAuthor("Missing arguments! Valid usage: !channeloptions filter [true/false]");
                channel.sendMessage(embed.build()).queue();
            }
            return;
        }

        if (args[1].equalsIgnoreCase("permanent")) {
            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("enable")) {
                    if (!Utils.isPermanentChannel(channel)) {
                        channel.getManager().putPermissionOverride(DiscordRole.BOT_PERMANENT_CHANNEL, EnumSet.of(Permission.VIEW_CHANNEL), null).queue();

                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                                .appendDescription("This channel (<#" + channel.getId() + ">) has been marked as a permanent channel.");
                        channel.sendMessage(embed.build()).queue();

                    } else {
                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED);
                        embed.setAuthor("This channel is already set to permanent mode!");
                        channel.sendMessage(embed.build()).queue();
                    }

                } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("disable")) {
                    if (Utils.isPermanentChannel(channel)) {
                        channel.getManager().removePermissionOverride(DiscordRole.BOT_PERMANENT_CHANNEL).queue();

                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                                .appendDescription("This channel (<#" + channel.getId() + ">) is not marked as a permanent channel anymore.");
                        channel.sendMessage(embed.build()).queue();

                    } else {
                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED);
                        embed.setAuthor("This channel is not set to permanent mode!");
                        channel.sendMessage(embed.build()).queue();
                    }
                } else {
                    EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                            .setAuthor("Invalid value! Valid usage: !channeloptions permanent [true/false]");
                    channel.sendMessage(embed.build()).queue();
                }
            } else {
                EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                        .setAuthor("Missing arguments! Valid usage: !channeloptions permanent [true/false]");
                channel.sendMessage(embed.build()).queue();
            }
            return;
        }

        if (args[1].equalsIgnoreCase("logging")) {
            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("enable")) {
                    if (!Utils.hasChatLogging(channel)) {
                        channel.getManager().removePermissionOverride(DiscordRole.BOT_NO_CHAT_LOGGING).queue();

                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                                .appendDescription("Chat Logging has been enabled for this channel (<#" + channel.getId() + ">).");
                        channel.sendMessage(embed.build()).queue();

                    } else {
                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED);
                        embed.setAuthor("This channel is already logging messages");
                        channel.sendMessage(embed.build()).queue();
                    }

                } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("disable")) {
                    if (Utils.hasChatLogging(channel)) {
                        channel.getManager().putPermissionOverride(DiscordRole.BOT_NO_CHAT_LOGGING, EnumSet.of(Permission.VIEW_CHANNEL), null).queue();

                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                                .appendDescription("Chat Logging has been disabled for this channel (<#" + channel.getId() + ">).");
                        channel.sendMessage(embed.build()).queue();

                    } else {
                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED);
                        embed.setAuthor("This channel is not logging messages!");
                        channel.sendMessage(embed.build()).queue();
                    }
                } else {
                    EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                            .setAuthor("Invalid value! Valid usage: !channeloptions logging [true/false]");
                    channel.sendMessage(embed.build()).queue();
                }
            } else {
                EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                        .setAuthor("Missing arguments! Valid usage: !channeloptions logging [true/false]");
                channel.sendMessage(embed.build()).queue();
            }
            return;
        }

        if (args[1].equalsIgnoreCase("staff")) {
            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("enable")) {
                    if (!Utils.isStaffChannel(channel)) {
                        channel.getManager().putPermissionOverride(DiscordRole.BOT_STAFF_CHANNEL, EnumSet.of(Permission.VIEW_CHANNEL), null).queue();

                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN)
                                .appendDescription("This channel (<#" + channel.getId() + ">) has been marked as a staff channel.");
                        channel.sendMessage(embed.build()).queue();

                    } else {
                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED);
                        embed.setAuthor("This channel is already a staff channel!");
                        channel.sendMessage(embed.build()).queue();
                    }

                } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("disable")) {
                    if (Utils.isStaffChannel(channel)) {
                        channel.getManager().removePermissionOverride(DiscordRole.BOT_STAFF_CHANNEL).queue();

                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.YELLOW)
                                .appendDescription("This channel (<#" + channel.getId() + ">) is not marked as a staff channel anymore.");
                        channel.sendMessage(embed.build()).queue();

                    } else {
                        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED);
                        embed.setAuthor("This channel is not a staff channel!");
                        channel.sendMessage(embed.build()).queue();
                    }
                } else {
                    EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                            .setAuthor("Invalid value! Valid usage: !channeloptions staff [true/false]");
                    channel.sendMessage(embed.build()).queue();
                }
            } else {
                EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                        .setAuthor("Missing arguments! Valid usage: !channeloptions staff [true/false]");
                channel.sendMessage(embed.build()).queue();
            }
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.appendDescription("This is not a valid channel option!\n\nValid options are: **staff**, **filter**, **logging**, **permanent**.");
        channel.sendMessage(embed.build()).queue();

    }
}
