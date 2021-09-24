package com.github.liamhbest0608;

import net.dv8tion.jda.api.JDA;

public class CommandAPI {

    private String prefix = "!";
    public static JDA jda;

    public CommandAPI(String prefix, JDA setJDA, boolean value){
        if (!prefix.equalsIgnoreCase("ignored")) {
            this.prefix = prefix;
        }

        if (value) {
            jda = setJDA;
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}