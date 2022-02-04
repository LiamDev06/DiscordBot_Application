package net.hybrid.discord.commands.admin;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;

import java.awt.*;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class ArchiveChannelCommand extends BotCommand {

    public ArchiveChannelCommand() {
        super("archivechannel");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        if (!Utils.hasRole(member, DiscordRole.ADMIN) && !Utils.hasRole(member, DiscordRole.OWNER))
            return;

        if (Utils.isPermanentChannel(channel)) {
            //TODO Send cannot do this due to permanent
            channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor("This channel is marked as permanent and therefore cannot be deleted!").build()).queue();
            return;
        }

        channel.sendMessage(":arrow_right: Moving channel to archive...").queue();

        ChannelManager manager = channel.getManager();
        Guild server = DiscordApplication.getInstance().getDiscordServer();

        String topic = "";
        if (channel.getTopic() != null) {
            topic = "**-** (" + channel.getTopic() + ")";
        }

        manager.clearOverridesAdded().queue();
        manager.clearOverridesRemoved().queue();

        for (PermissionOverride override : channel.getPermissionOverrides()) {
            if (override.isMemberOverride()) {
                override.delete().queue();
            }
        }

        for (PermissionOverride override : channel.getPermissionOverrides()) {
            if (override.isRoleOverride() && override.getRole() != DiscordRole.ADMIN) {
                override.delete().queue();
            }
        }

        manager.setParent(server.getCategoryById(914287680135790602L))
                .setTopic("**Archived Channel** " + topic)

                .putPermissionOverride(server.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .putPermissionOverride(DiscordRole.ADMIN, EnumSet.of(Permission.VIEW_CHANNEL), null)

                .putPermissionOverride(DiscordRole.BOT_NO_CHAT_FILTER, EnumSet.of(Permission.VIEW_CHANNEL), null)
                .putPermissionOverride(DiscordRole.BOT_NO_CHAT_LOGGING, EnumSet.of(Permission.VIEW_CHANNEL), null)
                .removePermissionOverride(DiscordRole.BOT_PERMANENT_CHANNEL.getIdLong())
                .putPermissionOverride(DiscordRole.BOT_STAFF_CHANNEL, EnumSet.of(Permission.VIEW_CHANNEL), null)

                .reason("Moved to archive via admin command").queueAfter(1, TimeUnit.SECONDS);

        channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle("Archived Channel")
                .appendDescription("This channel is now marked as an archived channel.\n\n")
                .appendDescription("**Issued by:** <@" + member.getIdLong() + ">")
                .build()).queue();

        Utils.getDiscordLogsChannel().sendMessage(new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle("Channel Archived")
                .appendDescription("Someone archived a channel via admin commands\n\n")
                .appendDescription("**Who:** <@" + member.getId() + ">\n")
                .appendDescription("**Channel:** <#" + channel.getId() + ">")
                .build()).queue();
    }

}












