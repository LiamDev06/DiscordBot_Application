package com.github.liamhbest0608;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BotCommand extends ListenerAdapter {

    private final String command;
    private boolean isSet = false;
    private final List<String> aliases = new ArrayList<String>();

    public BotCommand(String command) {
        this.command = command;

        if (!isSet) {
            CommandAPI.jda.addEventListener(this);
            isSet = true;
        }
    }

    public BotCommand(String command, String... aliases) {
        this.command = command;

        this.aliases.addAll(Arrays.stream(aliases).toList());

        if (!isSet) {
            CommandAPI.jda.addEventListener(this);
            isSet = true;
        }
    }

    public abstract void onCommand(Member member, Message message, TextChannel channel, String[] args);

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        CommandAPI commandAPI = new CommandAPI("ignored", null, false);
        String prefix = commandAPI.getPrefix();
        String message = event.getMessage().getContentRaw();
        String[] args = message.split("\\s+");

        if (message.startsWith(prefix)) {
            if (aliases.size() != 0) {
                for (String s : aliases) {
                    if (args[0].equalsIgnoreCase(prefix + s)) {
                        this.onCommand(
                                event.getMember(),
                                event.getMessage(),
                                event.getChannel(),
                                args
                        );
                        break;
                    }
                }
            }

            if (args[0].equalsIgnoreCase(prefix + command)) {
                this.onCommand(
                        event.getMember(),
                        event.getMessage(),
                        event.getChannel(),
                        args
                );
            }
        }
    }
}