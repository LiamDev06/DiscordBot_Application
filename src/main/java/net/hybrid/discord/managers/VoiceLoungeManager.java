package net.hybrid.discord.managers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utils.DiscordRole;
import net.hybrid.discord.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class VoiceLoungeManager extends ListenerAdapter {

    private final FileConfiguration config = DiscordApplication.getInstance().getConfig();

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        VoiceChannel channel = event.getChannelJoined();
        if (!channel.getParent().getName().contains("Voice Lounge")) return;

        if (channel.getName().contains("Create Voice Party")) {
            VoiceChannel target = event.getGuild().createVoiceChannel(event.getMember().getEffectiveName() + "'s Channel",
                    event.getGuild().getCategoryById(880208064702185525L))

                    .addMemberPermissionOverride(event.getMember().getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_ADD_REACTION))

                    .addPermissionOverride(DiscordRole.HELPER, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.MODERATOR, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.SENIOR_MODERATOR, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.ADMIN, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.OWNER, EnumSet.of(Permission.VIEW_CHANNEL), null)

                    .reason("Voice party created")
                    .complete();

            event.getGuild().moveVoiceMember(event.getMember(), target).queue();

            TextChannel targetText = event.getGuild().createTextChannel(event.getMember().getEffectiveName() + "-vctext",
                    event.getGuild().getCategoryById(880208064702185525L))
                    .setNSFW(false)
                    .setTopic("This is an auto-generated channel to use as a Voice text channel for " + event.getMember().getEffectiveName() + "'s voice party.")

                    .addMemberPermissionOverride(event.getMember().getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_ADD_REACTION))

                    .addPermissionOverride(DiscordRole.HELPER, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.MODERATOR, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.SENIOR_MODERATOR, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.ADMIN, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.OWNER, EnumSet.of(Permission.VIEW_CHANNEL), null)

                    .reason("Auto-generated text channel for voice party")
                    .complete();

            targetText.sendMessage("<@" + event.getMember().getId() + ">\n" +
                    "**Welcome to your VC text channel!**\nThis is an auto-generated channel that everyone in your voice party will have access to.\n\n" +
                    "➤ Invite members to your voice party with **!vcparty invite <user>**. Remove their access by doing **!vcparty remove <user>**.\n" +
                    "➤ The voice party will automatically be disbanded when the voice party creator leaves the VC").queue();
            return;
        }

        if (channel.getName().contains("'s Channel")) return;

        String id = channel.getName().replace("Voice Lounge ", "").replace("[#", "").replace("]", "");

        if (!this.config.getBoolean("hasTextChannel.voiceLounge" + id)) {
            TextChannel textChannel = event.getGuild().createTextChannel("voicelounge" + id + "-text",
                    event.getGuild().getCategoryById(880208064702185525L))
                    .setNSFW(false)
                    .setTopic("This is an auto-generated channel to use as a Voice text channel for the VC: **" + channel.getName() + "**.")

                    .addMemberPermissionOverride(event.getMember().getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_ADD_REACTION))

                    .addPermissionOverride(DiscordRole.HELPER, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.MODERATOR, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.SENIOR_MODERATOR, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.ADMIN, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(DiscordRole.OWNER, EnumSet.of(Permission.VIEW_CHANNEL), null)

                    .reason("Auto-generated voice text channel for channel " + channel.getName())
                    .complete();

            this.config.set("hasTextChannel.voiceLounge" + id, true);
            DiscordApplication.getInstance().saveConfig();

            textChannel.sendMessage("**Welcome to your VC text channel!**\nThis is an auto-generated channel that everyone in the VC '**" + channel.getName() + "**' will have access to.\n" +
                    "This channel will automatically be disbanded if the VC is empty.").queue();
        } else {

            TextChannel textChannel = event.getGuild().getTextChannelsByName("voicelounge" + id + "-text", true).get(0);
            if (textChannel != null) {
                textChannel.getManager().putMemberPermissionOverride(event.getMember().getIdLong(),
                        EnumSet.of(Permission.VIEW_CHANNEL), null)
                        .reason("Member added to voice lounge").queue();
            }

        }
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        VoiceChannel channel = event.getOldValue();
        if (!channel.getParent().getName().contains("Voice Lounge")) return;

        if (channel.getName().contains(event.getMember().getEffectiveName()) &&
            channel.getName().contains("Channel")) {
            if (!Utils.isPermanentChannel(channel)) {
                channel.delete().queue();
            }

            for (GuildChannel guildChannel : event.getGuild().getCategoryById(880208064702185525L).getChannels()) {
                if (guildChannel instanceof TextChannel) {
                    TextChannel textTarget = (TextChannel) guildChannel;

                    if (textTarget.getName().replace("-", " ").toLowerCase().contains(event.getMember().getEffectiveName().toLowerCase()) &&
                            textTarget.getName().endsWith("-vctext")) {

                        if (!Utils.isPermanentChannel(textTarget)) {
                            textTarget.delete().reason("VC text deleted due to no more members in the target voice channel").queue();
                        }
                        break;
                    }
                }
            }

            return;
        }

        String id = channel.getName().replace("Voice Lounge ", "").replace("[#", "").replace("]", "");
        TextChannel textChannel;
        try {
            textChannel = event.getGuild().getTextChannelsByName("voicelounge" + id + "-text", true).get(0);
        } catch (IndexOutOfBoundsException exception) { return; }

        for (PermissionOverride override : textChannel.getMemberPermissionOverrides()) {
            if (override.isMemberOverride() && override.getMember().getId().equalsIgnoreCase(event.getMember().getId())) {
                textChannel.getManager()
                        .removePermissionOverride(override.getIdLong())
                        .reason("Removed from VC text channel due to leaving target voice channel").queue();
            }
        }

        if (channel.getMembers().size() == 0) {
            if (!Utils.isPermanentChannel(textChannel)) {
                textChannel.delete().reason("VC text deleted due to no more members in the target voice channel").queue();
                config.set("hasTextChannel.voiceLounge" + id, false);
                DiscordApplication.getInstance().saveConfig();
            }
        }


    }
}











