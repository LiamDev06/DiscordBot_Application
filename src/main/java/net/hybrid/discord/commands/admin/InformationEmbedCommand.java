package net.hybrid.discord.commands.admin;

import com.github.liamhbest0608.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hybrid.discord.utility.DiscordRole;
import net.hybrid.discord.utility.Utils;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class InformationEmbedCommand extends BotCommand {

    public InformationEmbedCommand() {
        super("informationadd");
    }

    @Override
    public void onCommand(Member member, Message message, TextChannel channel, String[] args) {
        if (!Utils.hasRole(member, DiscordRole.DISCORD_BOT) && !Utils.hasRole(member, DiscordRole.OWNER))
            return;
        final String newLine = "\n";
        message.delete().reason("Information embed command message removed").queue();

        EmbedBuilder linking = new EmbedBuilder();
        linking.setColor(Color.ORANGE);
        linking.setTitle(":link: __Account Linking__");
        linking.appendDescription("Linking your account is not mandatory but we strongly encourage you to do it. Upon linking, you will " +
                "receive the corresponding rank you have on the network, on the discord as well.\n\n");
        linking.appendDescription("**1** - Please enter the Hybrid minecraft server and perform `/linkdiscord` and click the link.\n\n");
        linking.appendDescription("**2** - You will be asked to log into your Discord account.\n\n");
        linking.appendDescription("**3** - Once logged in, you will be asked if you wish to grant permission for Hybrid to know your Discord ID.\n\n");
        linking.appendDescription("**4** - After accepting this, you will be shown your Discord name and number (e.g. McNugget#3333). If it is the right one, please click the appropriate option to confirm.\n\n");
        linking.appendDescription("**5** - Linking complete! You will now automatically receive your matching network rank on the Discord server shortly after. Enjoy!");

        EmbedBuilder discordRules = new EmbedBuilder();
        discordRules.setColor(Color.RED);
        discordRules.setTitle(":no_entry: __Discord Rules__");
        discordRules.appendDescription("We want our discord to feel welcoming to everyone. These are the discord rules");
        discordRules.appendDescription(" that you are required to follow. Breaking these rules will result in a punishment!\n\n");
        discordRules.appendDescription("**1** - Harassment, discrimination, hate speech or abuse is strictly not allowed."+newLine+newLine);
        discordRules.appendDescription("**2** - No excessive swearing or spamming. Just don't bypass the filter :shrug:."+newLine+newLine);
        discordRules.appendDescription("**3** - Respect all users, players and staff."+newLine+newLine);
        discordRules.appendDescription("**4** - Inappropriate profile pictures, statuses, game activities or usernames are not allowed."+newLine+newLine);
        discordRules.appendDescription("**5** - Avoid pinging staff or users that currently obtain YouTuber, Twitch Streamer or Partner rank. Only ping them for emergencies!"+newLine+newLine);
        discordRules.appendDescription("**6** - Only send “clean” and appropriate links and files."+newLine+newLine);
        discordRules.appendDescription("**7** - Do not advertise your social media or other similar communities."+newLine+newLine);
        discordRules.appendDescription("**8** - Please behave in our voice channels, have an appropriate volume and language."+newLine+newLine);
        discordRules.appendDescription("**9** - NSFW, sexual, or similar topics are strictly prohibited."+newLine+newLine);
        discordRules.appendDescription("**10** - Sharing personal information is not allowed, not yours or someone else’s."+newLine+newLine);
        discordRules.appendDescription("**11** - Threatening other users, players or staff is strictly forbidden!"+newLine+newLine);
        discordRules.appendDescription("**12** - Hybrid follows Discord’s Terms of Service and Community Guidelines. You will be punished on the discord if you do not follow these."+newLine+newLine);
        discordRules.appendDescription(":round_pushpin: **Discord ToS:** https://discord.gg/terms\n:round_pushpin: **Discord Community Guidelines:** https://discord.com/guidelines" + newLine + newLine+newLine);
        discordRules.appendDescription("Remember to always report rule breakers. To report a discord rule breaker, use `!report <user> <reason>` in any of our channels.");

        EmbedBuilder serverRules = new EmbedBuilder();
        serverRules.setColor(Color.RED);
        serverRules.setTitle(":no_entry:  __Minecraft Server Rules__");
        serverRules.appendDescription("We want our minecraft server to feel welcoming to everyone. These are the minecraft rules");
        serverRules.appendDescription(" that you are required to follow. Breaking these rules will result in a punishment!"+newLine+newLine);
        serverRules.appendDescription("**1** - Clients or modifications that results in giving you an unfair advantage over other players is strictly forbidden and will get you banned!"+newLine+newLine);
        serverRules.appendDescription("**2** - No harassment, discrimination, hate speech or abuse is allowed."+newLine+newLine);
        serverRules.appendDescription("**3** - Respect all players and staff."+newLine+newLine);
        serverRules.appendDescription("**4** - No punishment (e.g mute or ban) evading."+newLine+newLine);
        serverRules.appendDescription("**5** - Abusing glitches or exploits **intentionally** is prohibited."+newLine+newLine);
        serverRules.appendDescription("**6** - No sharing of personal information, not yours or someone else's."+newLine+newLine);
        serverRules.appendDescription("**7** - No excessive swearing or spamming. Just don't bypass our filter!"+newLine+newLine);
        serverRules.appendDescription("**8** - Game sabotaging is not allowed! This includes things like cross teaming or trying to scam someone in Survival."+newLine+newLine);
        serverRules.appendDescription("**9** - Inappropriate usernames, capes or skins are not allowed."+newLine+newLine+newLine);
        serverRules.appendDescription("Remember to always report rule breakers. To report a minecraft rule breaker, use `/report <player> <reason>` in-game.");

        EmbedBuilder general = new EmbedBuilder();
        general.setColor(Color.CYAN);
        general.setTitle(":newspaper: __General Information__");

        general.appendDescription("Here is some good-to-know information you might be in need of." + newLine + newLine);

        general.appendDescription("**Report Rule Breakers**\n");
        general.appendDescription(":pushpin: Use `!report <user> <reason>` in any of our channels to report a discord rule breaker.\n\n");
        general.appendDescription(":pushpin: Use `/report <player> <reason>` on the Minecraft server to report a server rule breaker.\n\n");
        general.appendDescription(":pushpin: Click the `report icon` on any message on the forums to report a forum post.");
        general.appendDescription(newLine + newLine);

        general.appendDescription("**Appeal Punishments**" + newLine);
        general.appendDescription("Appeals are handled on our forums. Please visit https://hybridplays.com/appeal for more information.");
        general.appendDescription(newLine + newLine);

        general.appendDescription("**Applications**" + newLine);
        general.appendDescription("Information regarding applications to various positions such as helper, developer, builder and more can be found in <#" + "894000432186277921" + ">.");
        general.appendDescription(newLine + newLine);

        general.appendDescription("**Support**" + newLine);
        general.appendDescription("To receive quick support, open a ticket in <#" + "880208064513445926" + ">. " +
                "You can also email __support@hybridplays.com__ or start a forums conversation with any of our helpers or moderators at https://hybridplays.com");
        general.appendDescription(newLine + newLine);

        general.appendDescription("**Report Bugs**" + newLine);
        general.appendDescription("Report any bugs or glitches you find in <#" + "891100808987435069" + ">. Rewards for major bugs will be handed out :eyes:");
        general.appendDescription(newLine + newLine);

        general.appendDescription("**Suggestions**" + newLine);
        general.appendDescription("If you have a suggestion of something you think could be a great addition, head over to <#" + "891100829652766750" +"> and let us know!");
        general.appendDescription(newLine + newLine);

        general.appendDescription("**Emergency Situations**" + newLine);
        general.appendDescription("If a major emergency ever were to occur, please ping the ones with <@&" + "916462935419785277" + ">. This could be major systems breaking, DDoS attacks, high-up staff accounts hacked or similar. __Abusing this will get you punished__.");

        channel.sendMessage("**Thank you for joining the Hybrid discord!** Please take your time to read through the" +
                " rules and information stated here before using our channels").complete();
        channel.sendMessage(linking.build()).queueAfter(1, TimeUnit.SECONDS);

        channel.sendMessage(discordRules.build()).queueAfter(2, TimeUnit.SECONDS);

        channel.sendMessage(serverRules.build()).queueAfter(3, TimeUnit.SECONDS);

        channel.sendMessage(general.build()).queueAfter(4, TimeUnit.SECONDS);

        channel.sendMessage("We hope that you have an amazing time here and " +
                "remember that our helpers and moderators will always be here for you! " +
                "**Enjoy your time playing!**").queueAfter(5, TimeUnit.SECONDS);
    }
}













