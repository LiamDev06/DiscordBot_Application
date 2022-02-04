package net.hybrid.discord.systems;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
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

public class Suggestions extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Permission permission = Permission.ADMINISTRATOR;
        if (event.getMember() == null) return;
        Member member = event.getMember();

        if (event.getMember().hasPermission(permission)
                && event.getMessage().getContentRaw().equalsIgnoreCase("!suggestionembedadd")) {
            event.getMessage().delete().reason("Command message").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.MAGENTA);
            embed.setTitle(":clipboard: SUGGESTIONS");
            embed.appendDescription("Have an idea on how to make Hybrid even better? We're all ears! " +
                    "Our main goal is to bring content our players want - so do not sit on your suggestion! Let us know, no matter how small or big!");
            embed.appendDescription("\n\n**➤ Click the button to make a suggestion**");

            event.getChannel().sendMessage(embed.build()).queue(message -> message.addReaction("U+1F4CB").queue());
            return;
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase("!closesuggestion")) {
            if (event.getChannel().getName().startsWith("suggestion-")) {
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
                                .reason("Suggestion closed").queue();

                        override.delete().reason("Suggestion closed").queue();
                    }
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.MAGENTA);
                embed.setTitle("Suggestion Closed");
                embed.appendDescription(":red_circle: **Status:** Closed\n");
                embed.appendDescription(":robot: **Closing Staff:** <@" + event.getAuthor().getId() + ">\n");
                embed.appendDescription(":bookmark_tabs: **Suggestion ID:** " + event.getChannel().getName().replace("suggestion-", ""));
                embed.appendDescription("\n\n");
                embed.appendDescription("This suggestion has been closed! The author for this suggestion no longer has access to it.");
                event.getChannel().sendMessage(embed.build()).queue();

                event.getChannel().getManager().setName(
                        "sclosed-" + event.getChannel().getName().replace("suggestion-", "")
                ).reason("Suggestion closed").queueAfter(1, TimeUnit.SECONDS);

                event.getChannel().getManager().setTopic("This suggestion is closed.").reason("Suggestion Closed").queue();
            }

            if (event.getChannel().getName().startsWith("sclosed-")) {
                if (!Utils.isStaff(member)) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.RED);
                    embed.setAuthor("Only staff member can perform this command!");
                    event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setAuthor("This suggestion is already closed!");
                event.getChannel().sendMessage(embed.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
            }
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase("!deletesuggestion")) {
            if (event.getChannel().getName().startsWith("suggestion-") || event.getChannel().getName().startsWith("sclosed-")) {
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
                embed.setAuthor("Deleting suggestion...");
                event.getChannel().sendMessage(embed.build())
                        .queue(message -> event.getChannel().delete().reason("Suggestion deleted")
                                .queueAfter(2, TimeUnit.SECONDS));
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getChannel().getName().equalsIgnoreCase("suggestions") && !event.getUser().isBot()) {
            event.getReaction().removeReaction(event.getUser()).queue();
            Guild guild = event.getGuild();

            assert DiscordRole.HELPER != null;
            assert DiscordRole.MODERATOR != null;
            assert DiscordRole.SENIOR_MODERATOR != null;
            assert DiscordRole.ADMIN != null;
            assert DiscordRole.OWNER != null;

            assert DiscordRole.BOT_NO_CHAT_FILTER != null;
            assert DiscordRole.BOT_NO_CHAT_LOGGING != null;

            TextChannel channel = guild.createTextChannel("suggestion-" + DiscordApplication.getInstance().getConfig().getString("id.suggestions"))
                    .setNSFW(false)
                    .setTopic("A new suggestion has been created! Status: Open")
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

                    .reason("Suggestion created by " + event.getMember().getEffectiveName())
                    .complete();
            channel.sendMessage("<@" + event.getUser().getId() + ">").queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setTitle("Suggestion Created");
            embed.appendDescription(":white_check_mark: **Status:** Open\n");
            embed.appendDescription(":robot: **Creator:** <@" + event.getUserId() + ">\n");
            embed.appendDescription(":calendar: **Created:** " + date() + "\n");
            embed.appendDescription(":bookmark_tabs: **Suggestion ID:** " + channel.getName().replace("suggestion-", ""));
            embed.appendDescription("\n\n");
            embed.appendDescription("Let us know what you'd like to add on Hybrid to make the network even better. If we like it, we'll put it into production!");
            channel.sendMessage(embed.build()).queue();

            EmbedBuilder logEmbed = new EmbedBuilder();
            logEmbed.setColor(Color.YELLOW);
            logEmbed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getUser().getEffectiveAvatarUrl());
            logEmbed.setTitle("Suggestion Created");
            logEmbed.appendDescription("**Creator:** <@" + event.getUserId() + ">\n");
            logEmbed.appendDescription("**Channel:** <#" + channel.getId() + ">\n\n");
            logEmbed.appendDescription("A user just created a new suggestion. Once they are done writing it, the review phase should start. " +
                    "Unless the suggestion is a troll suggestion or similar, please include admins in this process.");
            Utils.getDiscordLogsChannel().sendMessage(logEmbed.build()).queue();

            DiscordApplication.getInstance().getConfig().set("id.suggestions",
                    DiscordApplication.getInstance().getConfig().getInt("id.suggestions") + 1);
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
