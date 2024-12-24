package dev.onebiteaidan.worldshop.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    private static JavaPlugin plugin;

    public static void setPlugin(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    private static void ensureInitialized() {
        if (plugin == null) {
            throw new IllegalStateException("Logger is not initialized. Call Logger.setPlugin() during plugin startup.");
        }
    }

    /**
     * Redirects the output of e.printStackTrace into the PaperMC logs.
     * @param exception to be redirected.
     */
    public static void logStacktrace(Exception exception) {
        ensureInitialized();
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        plugin.getLogger().severe(sw.toString());
    }

    /**
     * Logs into the PaperMC logs with the severe tag.
     * @param input to put into the logs.
     */
    public static void severe(String input) {
        ensureInitialized();
        plugin.getLogger().severe(input);
    }

    /**
     * Logs into the PaperMC logs with the warning tag.
     * @param input to put into the logs.
     */
    public static void warning(String input) {
        ensureInitialized();
        plugin.getLogger().warning(input);
    }

    /**
     * Logs into the PaperMC logs with the info tag.
     * @param input to put into the logs.
     */
    public static void info(String input) {
        ensureInitialized();
        plugin.getLogger().info(input);
    }
}