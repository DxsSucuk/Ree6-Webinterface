package de.presti.ree6.webinterface.utils;

import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    FileConfiguration cfg;

    public void init() {

            cfg = getConfig();

        if (!getFile().exists()) {
            cfg.options().copyDefaults(true);
            cfg.options().copyHeader(true);
            cfg.addDefault("mysql.user", "root");
            cfg.addDefault("mysql.db", "root");
            cfg.addDefault("mysql.pw", "yourpw");
            cfg.addDefault("mysql.host", "localhost");
            cfg.addDefault("mysql.port", 3306);

            try {
                cfg.save(getFile());
            } catch (Exception ex) {
            }

        }
    }

    public FileConfiguration getConfig() {
       return YamlConfiguration.loadConfiguration(getFile());
    }

    public File getFile() {
        return new File("config.yml");
    }

}
