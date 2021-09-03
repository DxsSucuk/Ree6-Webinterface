package de.presti.ree6.webinterface.utils;

import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    /**
     * Instance of the Config.
     */
    FileConfiguration cfg;

    /**
     * Initialize the Config.
     */
    public void init() {

        cfg = getConfig();

        // If Config File doesn't exist created a new Config File with the Default Values.
        if (!getFile().exists()) {
            cfg.options().copyDefaults(true);
            cfg.options().copyHeader(true);
            cfg.addDefault("mysql.user", "root");
            cfg.addDefault("mysql.db", "root");
            cfg.addDefault("mysql.pw", "yourpw");
            cfg.addDefault("mysql.host", "localhost");
            cfg.addDefault("mysql.port", 3306);
            cfg.addDefault("discord.client_id", "your application id");
            cfg.addDefault("discord.client_secret", "your application secret");

            // Save created Config as File.
            try {
                cfg.save(getFile());
            } catch (Exception ignore) {
            }

        }
    }

    /**
     * Create a new Instance of the Config.
     *
     * @return FileConfiguration    Returns a new Instance of a YamlConfiguration and loads the Data from the File.
     */
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }

    /**
     * Get Config File.
     *
     * @return File     Returns the Config File.
     */
    public File getFile() {
        return new File("config.yml");
    }

}
