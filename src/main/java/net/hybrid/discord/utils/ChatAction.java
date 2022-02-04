package net.hybrid.discord.utils;

public enum ChatAction {

    BLACKLISTED("BLACKLISTED"),
    DELETION("DELETION"),
    EDIT("EDIT"),
    MESSAGE_LENGTH("MESSAGE LENGTH");

    private final String name;

    ChatAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
