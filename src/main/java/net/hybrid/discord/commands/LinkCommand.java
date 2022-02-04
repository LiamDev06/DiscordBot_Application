package net.hybrid.discord.commands;

import com.github.liamhbest0608.BotCommand;
import com.hybridplays.core.CorePlugin;
import com.hybridplays.core.data.Mongo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.utils.DiscordLinkManager;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.UUID;

public class LinkCommand extends BotCommand {

    public LinkCommand() {
        super("link");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        message.delete().queue();
        Mongo mongo = CorePlugin.getInstance().getMongo();

        if (mongo.loadDocument("discordLinks", "discordId", member.getId()) != null) {
            channel.sendMessage("<@" + member.getId() + "> **-==-** `!link`").setEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Already Linked!** You are already linked. Perform `/discordunlink` on the Minecraft server to unlink.").build()).queue();
            return;
        }

        if (args.length == 1) {
            channel.sendMessage("<@" + member.getId() + "> **-==-** `!link`").setEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Missing Arguments!** Please specify a code with `!link <code>`.").build()).queue();
            return;
        }

        String code = args[1].trim();
        Document document = mongo.loadDocument("discordLinks", "code", code);

        if (document == null) {
            channel.sendMessage("<@" + member.getId() + "> **-==-** `!link`").setEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Invalid Code!** This code is invalid or could not be found in the database.").build()).queue();
            return;
        }

        if (System.currentTimeMillis() > document.getLong("expiresTimestamp")) {
            channel.sendMessage("<@" + member.getId() + "> **-==-** `!link`").setEmbeds(new EmbedBuilder().setColor(Color.RED)
                    .appendDescription("**Code Expired!** This code has expired. Perform `/discordlink` on the Minecraft server to receive a new one.").build()).queue();
            return;
        }

        document.replace("discordId", member.getId());
        document.replace("isLinked", true);
        mongo.saveDocument("discordLinks", document, "code", code);

        channel.sendMessage("<@" + member.getId() + ">").setEmbeds(new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("The linking has now been completed!")
                .setAuthor(member.getEffectiveName() + "#" + member.getUser().getDiscriminator(), null, member.getUser().getEffectiveAvatarUrl())
                .appendDescription(
                        "You are now linked with the Minecraft account `" + Bukkit.getOfflinePlayer(UUID.fromString(document.getString("playerUuid"))).getName() + "`.\n" +
                        "You can perform **/discordunlink** at any time on the Minecraft server to unlink.")
                .build()).queue();

        DiscordLinkManager.executeLinkActions(document, member);
    }
}














