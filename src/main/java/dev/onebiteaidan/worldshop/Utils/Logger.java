package dev.onebiteaidan.worldshop.Utils;

import dev.onebiteaidan.worldshop.WorldShop;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    /**
     * Redirects the output of e.printStackTrace into the PaperMC logs.
     * @param exception to be redirected.
     */
    public static void logStacktrace(Exception exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        WorldShop.getPlugin(WorldShop.class).getLogger().severe(sw.toString());
    }

    /**
     * Logs into the PaperMC logs with the severe tag.
     * @param input to put into the logs.
     */
    public static void severe(String input) {
        WorldShop.getPlugin(WorldShop.class).getLogger().severe(input);
    }


}
