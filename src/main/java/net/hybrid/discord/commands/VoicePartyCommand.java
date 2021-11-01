package net.hybrid.discord.commands;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.hybrid.discord.DiscordApplication;

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

            if (args[1].equalsIgnoreCase("invite")) {
                if (args.length >= 3) {
                    String invite = args[2];
                    boolean found = false;

                    for (Member target : DiscordApplication.getInstance().getDiscordServer().getMembers()) {
                        if (target.getEffectiveName().equalsIgnoreCase(invite)) {
                            found = true;
                            if (isAlreadyMember(channel, target)) {
                                channel.sendMessage("**USER ALREADY INVITED!** The user " + target.getEffectiveName() + " is already a member of this voice party!").queue();
                                return;
                            }

                            channel.getManager().putMemberPermissionOverride(
                                    target.getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL), null
                            ).queue();

                            for (GuildChannel targetChannel : DiscordApplication.getInstance().getDiscordServer().getCategoryById(880208064702185525L).getChannels()) {
                                if (targetChannel instanceof VoiceChannel) {
                                    if (targetChannel.getName().contains("Channel") && targetChannel.getName().contains(member.getEffectiveName())) {
                                        VoiceChannel voice = (VoiceChannel) targetChannel;
                                        voice.getManager().putMemberPermissionOverride(target.getIdLong(),
                                                EnumSet.of(Permission.VIEW_CHANNEL), null).queue();
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
                    String invite = args[2];
                    boolean found = false;

                    for (Member target : DiscordApplication.getInstance().getDiscordServer().getMembers()) {
                        if (target.getEffectiveName().equalsIgnoreCase(invite)) {
                            found = true;
                            if (!isAlreadyMember(channel, target)) {
                                channel.sendMessage("**USER IS NOT A MEMBER!** The user " + target.getEffectiveName() + " is already not a member of this party, and therefore cannot be removed!").queue();
                                return;
                            }

                            channel.getManager().putMemberPermissionOverride(
                                    target.getIdLong(), null, EnumSet.of(Permission.VIEW_CHANNEL)
                            ).queue();

                            for (GuildChannel targetChannel : DiscordApplication.getInstance().getDiscordServer().getCategoryById(880208064702185525L).getChannels()) {
                                if (targetChannel instanceof VoiceChannel) {
                                    if (targetChannel.getName().contains("Channel") && targetChannel.getName().contains(member.getEffectiveName())) {
                                        VoiceChannel voice = (VoiceChannel) targetChannel;
                                        voice.getManager().putMemberPermissionOverride(target.getIdLong(),
                                                null, EnumSet.of(Permission.VIEW_CHANNEL)).queue();
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






