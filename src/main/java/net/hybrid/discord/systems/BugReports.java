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
import net.hybrid.discord.filters.ChatActionEvents;
import net.hybrid.discord.utility.DiscordRole;
import net.hybrid.discord.utility.Utils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class BugReports extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Permission permission = Permission.ADMINISTRATOR;
        if (event.getMember() == null) return;
        Member member = event.getMember();

        if (event.getMember().hasPermission(permission)
                && event.getMessage().getContentRaw().equalsIgnoreCase("!bugreportsembedadd")) {
            event.getMessage().delete().reason("Deleted due to command message").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setTitle(":desktop: BUG REPORTS");
            embed.appendDescription("Found something that shouldn't work? Please go ahead and send in a bug report so our team can review." +
                    " Please include details and any relevant screenshots, links, videos or similar that is needed to re-create the bug.\n\n");
            embed.appendDescription(":round_pushpin: Rewards for major bugs and issues will be handed out");
            embed.appendDescription("\n\n**➤ Click the button to submit a bug report**");

            event.getChannel().sendMessage(embed.build()).queue(message -> message.addReaction("U+1F5A5").queue());
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase("!closebug")) {
            if (event.getChannel().getName().startsWith("bug-")) {
                ChatActionEvents.shouldNotSendDeleted.add(event.getMessageId());
                event.getMessage().delete().reason("Deleted due to command message").queue();

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
                                .reason("Bug report closed, user issuer removed").queue();

                        override.delete().reason("Bug report closed, user issuer removed").queue();
                    }
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.YELLOW);
                embed.setTitle("Bug Report Closed");
                embed.appendDescription(":red_circle: **Status:** Closed\n");
                embed.appendDescription(":robot: **Closing Staff:** <@" + event.getAuthor().getId() + ">\n");
                embed.appendDescription(":bookmark_tabs: **Bug ID:** " + event.getChannel().getName().replace("bug-", ""));
                embed.appendDescription("\n\n");
                embed.appendDescription("This bug report has been closed! The author for this report no longer has access to it.");
                event.getChannel().sendMessage(embed.build()).queue();

                event.getChannel().getManager().setName(
                        "bclosed-" + event.getChannel().getName().replace("bug-", "")
                ).reason("Bug report closed").queueAfter(1, TimeUnit.SECONDS);

                event.getChannel().getManager().setTopic("This bug report is closed.").reason("Bug report closed").queue();
            }

            if (event.getChannel().getName().startsWith("bclosed-")) {
                ChatActionEvents.shouldNotSendDeleted.add(event.getMessageId());
                event.getMessage().delete().reason("Deleted due to command message").queue();

                if (!Utils.isStaff(member)) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.RED);
                    embed.setAuthor("Only staff member can perform this command!");
                    event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setAuthor("This bug report is already closed!");
                event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
            }
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase("!deletebug")) {
            if (event.getChannel().getName().startsWith("bug-") || event.getChannel().getName().startsWith("bclosed-")) {
                ChatActionEvents.shouldNotSendDeleted.add(event.getMessageId());
                event.getMessage().delete().reason("Deleted due to command message").queue();

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
                embed.setAuthor("Deleting bug report...");
                event.getChannel().sendMessage(embed.build())
                        .queue(message -> event.getChannel().delete().reason("Bug report deleted")
                                .queueAfter(2, TimeUnit.SECONDS));
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getChannel().getName().equalsIgnoreCase("bug-reports") && !event.getUser().isBot()) {
            event.getReaction().removeReaction(event.getUser()).queue();
            Guild guild = event.getGuild();

            assert DiscordRole.HELPER != null;
            assert DiscordRole.MODERATOR != null;
            assert DiscordRole.SENIOR_MODERATOR != null;
            assert DiscordRole.ADMIN != null;
            assert DiscordRole.OWNER != null;

            assert DiscordRole.BOT_NO_CHAT_FILTER != null;
            assert DiscordRole.BOT_NO_CHAT_LOGGING != null;

            TextChannel channel = guild.createTextChannel("bug-" + DiscordApplication.getInstance().getConfig().getString("id.bugReports"))
                    .setNSFW(false)
                    .setTopic("A new bug report has been created! Status: Open")
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

                    .reason("Bug report created by " + event.getMember().getEffectiveName())
                    .complete();
            channel.sendMessage("<@" + event.getUser().getId() + ">").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.CYAN);
            embed.setTitle("Bug Report Opened");
            embed.appendDescription(":white_check_mark: **Status:** Open\n");
            embed.appendDescription(":robot: **Creator:** <@" + event.getUserId() + ">\n");
            embed.appendDescription(":calendar: **Created:** " + date() + "\n");
            embed.appendDescription(":bookmark_tabs: **Bug ID:** " + channel.getName().replace("bug-", ""));
            embed.appendDescription("\n\n");
            embed.appendDescription("Explain the bug with details. Someone from our team will review the bug once done.");
            channel.sendMessage(embed.build()).queue();

            EmbedBuilder logEmbed = new EmbedBuilder();
            logEmbed.setColor(Color.GREEN);
            logEmbed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getUser().getEffectiveAvatarUrl());
            logEmbed.setTitle("Bug Report Opened");
            logEmbed.appendDescription("**Creator:** <@" + event.getUserId() + ">\n");
            logEmbed.appendDescription("**Channel:** <#" + channel.getId() + ">\n\n");
            logEmbed.appendDescription("A user just opened a bug report. The bug needs to be re-created internally in the team to verify its existence. " +
                    "If the bug is valid and real, contact admins for further information.");
            Utils.getDiscordLogsChannel().sendMessage(logEmbed.build()).queue();

            DiscordApplication.getInstance().getConfig().set("id.bugReports",
                    DiscordApplication.getInstance().getConfig().getInt("id.bugReports") + 1);
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
