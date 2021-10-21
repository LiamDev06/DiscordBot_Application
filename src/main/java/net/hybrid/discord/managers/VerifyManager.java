package net.hybrid.discord.managers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.utility.DiscordRole;
import net.hybrid.discord.utility.Utils;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class VerifyManager extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        Permission permission = Permission.ADMINISTRATOR;

        if (event.getMember().hasPermission(permission)
                && event.getMessage().getContentRaw().equalsIgnoreCase("!verifyadd")) {

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setTitle("Verify");
            embed.appendDescription("Welcome to the official Hybrid Discord! ");
            embed.appendDescription("Before you can access our channels we require you to verify yourself ");
            embed.appendDescription("and make sure that you are a human. This will help us with keeping ");
            embed.appendDescription("bot free with real and active members! Thank you!\n\n");
            embed.appendDescription("**Server IP:** hybridplays.com\n");
            embed.appendDescription("**Website/Forums:** https://hybridplays.com\n");
            embed.appendDescription("**Network Announcements:** <#880208195845509190>\n");
            embed.appendDescription("**Information and Rules:** <#880208064211484691>");

            event.getMessage().delete().queue();
            event.getChannel().sendMessage(embed.build()).queue(message -> message.addReaction("U+2705").queue());
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
        long verificationChannelID = 891100362809942026L;

        if (event.getChannel().getIdLong() == verificationChannelID){
            if (!event.getUser().isBot()) {
                Role unverified = DiscordRole.UNVERIFIED;
                Role verified = DiscordRole.VERIFIED;

                if (unverified == null) return;
                if (verified == null) return;

                event.getGuild().addRoleToMember(event.getUserId(), verified).queue();
                event.getGuild().removeRoleFromMember(event.getUserId(), unverified).queueAfter(1, TimeUnit.SECONDS);

                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
