package com.forgeessentials.chat;

import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.util.OutputHandler;

public class ChatConfig extends ConfigLoaderBase
{

    private static final String CATEGORY = ModuleChat.CONFIG_CATEGORY;

    private static final String CAT_GM = CATEGORY + ".Gamemodes";

    public static final String CHAT_FORMAT_HELP = "Format for chat. Always needs to contain all 5 \"%s\" placeholders like the default!";

    private static final String MUTEDCMD_HELP = "All commands in here will be blocked if the player is muted.";

    private static final String WELCOME_MESSAGE = "Welcome messages for new players. Can be colour formatted (supports script arguments)";

    private static final String LOGIN_MESSAGE = "Login message shown each time the player logs in (supports script arguments)";

    public static String gamemodeCreative;

    public static String gamemodeAdventure;

    public static String gamemodeSurvival;

    public static String chatFormat = "%s%s<%s>%s%s ";

    public static String welcomeMessage;

    public static String[] loginMessage;

    public static Set<String> mutedCommands = new HashSet<>();

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment("Chat", "Chat configuration");

        try
        {
            chatFormat = config.get("Chat", "ChatFormat", "%s%s<%s>%s%s ", CHAT_FORMAT_HELP).getString();
            String.format(chatFormat, "", "", "", "", "");
        }
        catch (IllegalFormatException e)
        {
            OutputHandler.felog.severe("Invalid chat format specified in chat config!");
            chatFormat = "%s%s<%s>%s%s ";
        }

        welcomeMessage = config.get("Chat", "WelcomeMessage", "Welcome %username to the server!", WELCOME_MESSAGE).getString();
        loginMessage = config.get("Chat", "LoginMessage", "Welcome %username to the server!", LOGIN_MESSAGE).getStringList();

        config.addCustomCategoryComment(CAT_GM, "Gamemode names");
        gamemodeSurvival = config.get(CAT_GM, "Survival", "survival").getString();
        gamemodeCreative = config.get(CAT_GM, "Creative", "creative").getString();
        gamemodeAdventure = config.get(CAT_GM, "Adventure", "adventure").getString();

        mutedCommands.clear();
        for (String cmd : config.get("Chat.mute", "mutedCommands", new String[] { "me" }, MUTEDCMD_HELP).getStringList())
            mutedCommands.add(cmd);

        ModuleChat.instance.setChatLogging(config.get(CATEGORY, "LogChat", true, "Log all chat messages").getBoolean(true));
    }

    @Override
    public void save(Configuration config)
    {
    }

}