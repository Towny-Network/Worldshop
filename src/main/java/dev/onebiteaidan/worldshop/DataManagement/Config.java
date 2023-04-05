package dev.onebiteaidan.worldshop.DataManagement;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

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
        create();
    }

    public Config(Plugin instance, String path, String name, boolean createIfNotExist, boolean resource) {
        this(instance, new File(path), name, createIfNotExist, resource);
    }

    public FileConfiguration getConfig() {
        return config;
    }


    //region Getters, variables and constructors

    public void save() {
        try {
            config.save(file);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public File reloadFile() {
        file = new File(path, name);
        return file;
    }

    public FileConfiguration reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
        return config;
    }

    public void reload() {
        reloadFile();
        reloadConfig();
    }

    public void create() {
        if (file == null) {
            reload();
        }
        if (!createIfNotExist || file.exists()) {
            return;
        }
        file.getParentFile().mkdirs();
        if (resource) {
            plugin.saveResource(name, false);
        } else {
            try {
                file.createNewFile();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        if (config == null) {
            reloadConfig();
        }
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
