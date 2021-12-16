package net.hybrid.discord.commands;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.hybrid.discord.DiscordApplication;
import net.hybrid.discord.utility.Utils;

import java.util.EnumSet;

public class VoicePartyCommand extends BotCommand {

    public VoicePartyCommand() {
        super("vcparty");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        if (channel.getName().endsWith("-vctext") && channel.getName().replace("-", " ").toLowerCase().contains(member.getEffectiveName().toLowerCase())) {

            if (args.length == 1) {
                channel.sendMessage("**MISSING ARGUMENTS!** Please specify a sub-command and a user, valid usage: !vcparty [invite/remove] [user]").queue();
                return;
            }

            if (args[1].equalsIgnoreCase("invite") || args[1].equalsIgnoreCase("add")) {
                if (args.length >= 3) {
                    StringBuilder inviteBuilder = new StringBuilder();
                    boolean found = false;

                    int count = 0;
                    for (String s : args) {
                        if (count > 1) {
                            inviteBuilder.append(s).append(" ");
                        }

                        count++;
                    }

                    String invite = inviteBuilder.toString().trim();

                    for (Member target : DiscordApplication.getInstance().getDiscordServer().getMembers()) {
                        if (target.getEffectiveName().equalsIgnoreCase(invite) || target.getUser().getName().equalsIgnoreCase(invite)) {
                            found = true;
                            if (member.getIdLong() == target.getIdLong()) {
                                channel.sendMessage("**CANNOT INVITE!** You cannot invite yourself to the voice party - you are already here!").queue();
                                return;
                            }

                            if (isAlreadyMember(channel, target)) {
                                channel.sendMessage("**USER ALREADY MEMBER!** The user " + target.getEffectiveName() + " is already a member of this voice party!").queue();
                                return;
                            }

                            channel.getManager().putMemberPermissionOverride(
                                    target.getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL), null
                            ).reason("Voice party invitation from " + member.getEffectiveName() + " to " + target.getEffectiveName()).queue();

                            for (GuildChannel targetChannel : DiscordApplication.getInstance().getDiscordServer().getCategoryById(880208064702185525L).getChannels()) {
                                if (targetChannel instanceof VoiceChannel) {
                                    if (targetChannel.getName().contains("Channel") && targetChannel.getName().contains(member.getEffectiveName())) {
                                        VoiceChannel voice = (VoiceChannel) targetChannel;
                                        voice.getManager().putMemberPermissionOverride(target.getIdLong(),
                                                EnumSet.of(Permission.VIEW_CHANNEL), null)
                                                .reason("Voice party invitation from " + member.getEffectiveName() + " to " + target.getEffectiveName()).queue();
                                        break;
                                    }
                                }
                            }

                            channel.sendMessage("<@" + target.getId() + ">").queue();
                            channel.sendMessage("You have been invited to **" + member.getEffectiveName() + "'s** voice party! Welcome!").queue();

                            break;
                        }
                    }

                    if (!found) {
                        channel.sendMessage("**USER NOT FOUND!** A user with the name __" + invite + "__ could not be found in the Hybrid discord server.").queue();
                    }

                } else {
                    channel.sendMessage("**MISSING ARGUMENTS!** Please specify a user to invite, valid usage: !vcparty [invite/remove] [user]").queue();
                }

            } else if (args[1].equalsIgnoreCase("remove")) {
                if (args.length >= 3) {
                    StringBuilder inviteBuilder = new StringBuilder();
                    boolean found = false;

                    int count = 0;
                    for (String s : args) {
                        if (count > 1) {
                            inviteBuilder.append(s).append(" ");
                        }

                        count++;
                    }

                    String invite = inviteBuilder.toString().trim();

                    for (Member target : DiscordApplication.getInstance().getDiscordServer().getMembers()) {
                        if (target.getEffectiveName().equalsIgnoreCase(invite) || target.getUser().getName().equalsIgnoreCase(invite)) {
                            found = true;
                            if (!isAlreadyMember(channel, target)) {
                                channel.sendMessage("**USER IS NOT A MEMBER!** The user " + target.getEffectiveName() + " is already not a member of this party, and therefore cannot be removed!").queue();
                                return;
                            }

                            if (member.getIdLong() == target.getIdLong()) {
                                channel.sendMessage("**CANNOT REMOVE!** You cannot remove yourself from the voice party!\nIf you want to disband the party, please leave the party voice channel.").queue();
                                return;
                            }

                            if (Utils.isStaff(target)) {
                                channel.sendMessage("**CANNOT REMOVE!** This member cannot be removed from the voice party!").queue();
                                return;
                            }

                            channel.getManager().putMemberPermissionOverride(
                                    target.getIdLong(), null, EnumSet.of(Permission.VIEW_CHANNEL)
                            ).reason("Voice party leader " + member.getEffectiveName() + " removed " + target.getEffectiveName() + " from their voice party").queue();

                            for (GuildChannel targetChannel : DiscordApplication.getInstance().getDiscordServer().getCategoryById(880208064702185525L).getChannels()) {
                                if (targetChannel instanceof VoiceChannel) {
                                    if (targetChannel.getName().contains("Channel") && targetChannel.getName().contains(member.getEffectiveName())) {
                                        VoiceChannel voice = (VoiceChannel) targetChannel;
                                        voice.getManager().putMemberPermissionOverride(target.getIdLong(),
                                                null, EnumSet.of(Permission.VIEW_CHANNEL))
                                                .reason("Voice party leader " + member.getEffectiveName() + " removed " + target.getEffectiveName() + " from their voice party").queue();
                                        break;
                                    }
                                }
                            }

                            channel.sendMessage("You removed **" + target.getEffectiveName() + "** from the voice party!").queue();

                            break;
                        }
                    }

                    if (!found) {
                        channel.sendMessage("**USER NOT FOUND!** A user with the name __" + invite + "__ could not be found in the Hybrid discord server.").queue();
                    }

                } else {
                    channel.sendMessage("**MISSING ARGUMENTS!** Please specify a user to remove, valid usage: !vcparty [invite/remove] [user]").queue();
                }
            } else {
                channel.sendMessage("**INVALID SUB-COMMAND!** Please use a valid sub-command, valid usage: !vcparty [invite/remove] [user]").queue();
            }
        }
    }

    private boolean isAlreadyMember(GuildChannel guildChannel, Member member) {
        boolean value = false;

        for (Member target : guildChannel.getMembers()) {
            if (target.getId().equalsIgnoreCase(member.getId())) {
                value = true;
                break;
            }
        }

        return value;
    }

}






