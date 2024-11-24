package dev.onebiteaidan.worldshop.Model.DataManagement;

import dev.onebiteaidan.worldshop.Utils.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static it.unimi.dsi.fastutil.io.FastBufferedOutputStream.DEFAULT_BUFFER_SIZE;

public class Config {
    protected final boolean createIfNotExist, resource;
    protected final Plugin plugin;
    protected static FileConfiguration config;
    protected File file, path;
    protected String name;

    /**
     * Config Constructor
     * @param instance Main plugin instance
     * @param path Filepath to store the config file in (typically in: this.getDataFolder())
     * @param name Name of the config file (.yml will be appended to the filename on creation)
     * @param createIfNotExist Creates the file if it doesn't exist
     * @param resource This is true if the config file is placed in the resources folder of the project
     */
    public Config(Plugin instance, File path, String name, boolean createIfNotExist, boolean resource) {
        this.plugin = instance;
        this.path = path;
        this.name = name + ".yml";
        this.createIfNotExist = createIfNotExist;
        this.resource = resource;

        file = new File(path.getAbsolutePath() + File.separator + this.name);

        if (!file.exists()) {
            try {
                copyInputStreamToFile(Objects.requireNonNull(plugin.getResource(this.name)), file);
            } catch(IOException e) {
                Logger.logStacktrace(e);
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    // Grabbed this function from: https://mkyong.com/java/how-to-convert-inputstream-to-file-in-java/
    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }


    //region Getters, variables and constructors

    public FileConfiguration reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
        return config;
    }
    //endregion


    // Methods to grab values from the config file
    //region Database methods
    public static String getDatabaseType() {
        return config.getString("Database.Type");
    }

    public static String getHost() {
        return config.getString("Database.Host");
    }

    public static int getPort() {
        return config.getInt("Database.Port");
    }

    public static String getDatabase() {
        return config.getString("Database.Database");
    }

    public static String getUsername() {
        return config.getString("Database.Username");
    }

    public static String getPassword() {
        return config.getString("Database.Password");
    }

    //endregion


}
