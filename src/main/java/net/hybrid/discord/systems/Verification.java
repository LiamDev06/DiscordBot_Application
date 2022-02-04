package net.hybrid.discord.systems;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;

import javax.annotation.Nonnull;
import java.awt.*;

public class Verification extends ListenerAdapter {

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equalsIgnoreCase("addverifyembed")) {
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

            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle("Verification")
                    .appendDescription("Welcome to the community discord of the Hybrid minecraft server! Before you can access our channels we require you to verify yourself so we can make sure you are a human. This helps us keeping bot free with only real and active members!")
                    .appendDescription("\n\n**Minecraft Server IP:** hybridplays.com")
                    .appendDescription("\n**Website:** https://hybridplays.com")
                    .appendDescription("\n**Network Announcements:** <#880208195845509190>")
                    .appendDescription("\n**Information and Rules:** <#880208064211484691>")
                    .build();

            target.sendMessageEmbeds(embed).setActionRow(Button.success("server_verify_button", "Verify")).queue();
        }
    }

    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if (event.getButton() == null || event.getButton().getId() == null ||
                event.getMember() == null || DiscordRole.VERIFIED == null
                || event.getGuild() == null || DiscordRole.UNVERIFIED == null) return;

        if (event.getButton().getId().equalsIgnoreCase("server_verify_button")) {
            //TODO: Make and check to make sure the player IS NOT muted

            event.getGuild().addRoleToMember(event.getMember(), DiscordRole.VERIFIED).reason("User verified in #verify").queue();
            event.getGuild().removeRoleFromMember(event.getMember(), DiscordRole.UNVERIFIED).reason("User verified in #verify").queue();

            event.getInteraction().deferReply(true).addEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN).appendDescription("You have **successfully verified** yourself, welcome!")
                    .build()).queue();
        }
    }



}














