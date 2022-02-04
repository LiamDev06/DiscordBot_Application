package net.hybrid.discord.systems;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class Support extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Permission permission = Permission.ADMINISTRATOR;
        if (event.getMember() == null) return;
        Member member = event.getMember();

        if (event.getMember().hasPermission(permission)
                && event.getMessage().getContentRaw().equalsIgnoreCase("!supportembedadd")) {
            event.getMessage().delete().reason("Command message").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.ORANGE);
            embed.setTitle(":notepad_spiral: SUPPORT");
            embed.appendDescription("Need an extra hand? No worries then, our staff team is here to help you!" +
                    " Create a new ticket, explain your matter, and someone from our staff team will reach out to you shortly.\n\n");
            embed.appendDescription(":round_pushpin: You can also email **__support@hybridplays.com__** for special support, e.g. chargebacks, store errors and similar.");
            embed.appendDescription("\n\n**➤ Click the button to submit a ticket**");

            event.getChannel().sendMessage(embed.build()).queue(message -> message.addReaction("U+1F5D2").queue());
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase("!closeticket")) {
            if (event.getChannel().getName().startsWith("support-")) {
                if (!Utils.isStaff(member)) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.RED);
                    embed.setAuthor("Only staff member can perform this command!");
                    event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                for (PermissionOverride override : event.getChannel().getMemberPermissionOverrides()) {
                    if (override.isMemberOverride()) {
                        event.getChannel().getManager()
                                .removePermissionOverride(override.getIdLong())
                                .reason("Support ticket closed").queue();

                        override.delete().reason("Support ticket closed").queue();
                    }
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.ORANGE);
                embed.setTitle("Ticket Closed");
                embed.appendDescription(":red_circle: **Status:** Closed\n");
                embed.appendDescription(":robot: **Closing Staff:** <@" + event.getAuthor().getId() + ">\n");
                embed.appendDescription(":bookmark_tabs: **Ticket ID:** " + event.getChannel().getName().replace("support-", ""));
                embed.appendDescription("\n\n");
                embed.appendDescription("This ticket has been closed! The author for this ticket no longer has access to it.");
                event.getChannel().sendMessage(embed.build()).queue();

                event.getChannel().getManager().setName(
                        "tclosed-" + event.getChannel().getName().replace("support-", "")
                ).reason("Support ticket closed").queueAfter(1, TimeUnit.SECONDS);

                event.getChannel().getManager().setTopic("This ticket is closed.")
                        .reason("Support ticket closed").queue();
            }

            if (event.getChannel().getName().startsWith("tclosed-")) {
                if (!Utils.isStaff(member)) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.RED);
                    embed.setAuthor("Only staff member can perform this command!");
                    event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setAuthor("This ticket is already closed!");
                event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
            }
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase("!deleteticket")) {
            if (event.getChannel().getName().startsWith("support-") || event.getChannel().getName().startsWith("tclosed-")) {
                if (!Utils.isStaff(member)) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.RED);
                    embed.setAuthor("Only staff member can perform this command!");
                    event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                if (Utils.isPermanentChannel(event.getChannel())) {
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).appendDescription(
                            "This channel is marked as a permanent one and therefore cannot be deleted!"
                    ).build()).queue();
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.GREEN);
                embed.setAuthor("Deleting ticket...");
                event.getChannel().sendMessage(embed.build())
                        .queue(message -> event.getChannel().delete().reason("Support ticket deleted")
                                .queueAfter(2, TimeUnit.SECONDS));
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getChannel().getName().equalsIgnoreCase("support") && !event.getUser().isBot()) {
            event.getReaction().removeReaction(event.getUser()).queue();
            Guild guild = event.getGuild();

            assert DiscordRole.HELPER != null;
            assert DiscordRole.MODERATOR != null;
            assert DiscordRole.SENIOR_MODERATOR != null;
            assert DiscordRole.ADMIN != null;
            assert DiscordRole.OWNER != null;

            assert DiscordRole.BOT_NO_CHAT_FILTER != null;
            assert DiscordRole.BOT_NO_CHAT_LOGGING != null;

            TextChannel channel = guild.createTextChannel("support-" + DiscordApplication.getInstance().getConfig().getString("id.support"))
                    .setNSFW(false)
                    .setTopic("A new support ticket has been created! Status: Open")
                    .setParent(guild.getCategoryById(887157431954714664L))
                    .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES), null)
                    .addPermissionOverride(DiscordRole.HELPER, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES), null)
                    .addPermissionOverride(DiscordRole.MODERATOR, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES), null)
                    .addPermissionOverride(DiscordRole.SENIOR_MODERATOR, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES), null)
                    .addPermissionOverride(DiscordRole.ADMIN, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES), null)
                    .addPermissionOverride(DiscordRole.OWNER, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES), null)

                    .addPermissionOverride(DiscordRole.BOT_NO_CHAT_FILTER, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.BOT_NO_CHAT_LOGGING, EnumSet.of(Permission.VIEW_CHANNEL), null)

                    .reason("Support ticket opened by " + event.getMember().getEffectiveName())
                    .complete();
            channel.sendMessage("<@" + event.getUser().getId() + ">").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.ORANGE);
            embed.setTitle("Ticket Opened");
            embed.appendDescription(":white_check_mark: **Status:** Open\n");
            embed.appendDescription(":robot: **Creator:** <@" + event.getUserId() + ">\n");
            embed.appendDescription(":calendar: **Created:** " + date() + "\n");
            embed.appendDescription(":bookmark_tabs: **Ticket ID:** " + channel.getName().replace("support-", ""));
            embed.appendDescription("\n\n");
            embed.appendDescription("Start explaining your issue, problem or other matter. Someone from our team will be with you shortly.");
            channel.sendMessage(embed.build()).queue();

            EmbedBuilder logEmbed = new EmbedBuilder();
            logEmbed.setColor(Color.CYAN);
            logEmbed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getUser().getEffectiveAvatarUrl());
            logEmbed.setTitle("Ticket Created");
            logEmbed.appendDescription("**Creator:** <@" + event.getUserId() + ">\n");
            logEmbed.appendDescription("**Channel:** <#" + channel.getId() + ">\n\n");
            logEmbed.appendDescription("A user just opened a ticket. Once they are done writing it, any staff member can start helping them. " +
                    "Contact higher-ranking staff if the issue cannot be resolved.");
            Utils.getDiscordLogsChannel().sendMessage(logEmbed.build()).queue();

            DiscordApplication.getInstance().getConfig().set("id.support",
                    DiscordApplication.getInstance().getConfig().getInt("id.support") + 1);
            DiscordApplication.getInstance().saveConfig();
        }
    }

    private String date() {
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        LocalDateTime now = LocalDateTime.now();

        // MM dd yyyy
        return day.format(now) + " " + getMonthForInt(Integer.parseInt(month.format(now)) - 1) + ", " + year.format(now);
    }

    private String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

}
