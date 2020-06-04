package pl.javatar.minecraft.vwhelpop.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageConfiguration
{
    private final FileConfiguration   config;
    private final Map<String, String> messages = new HashMap<>();

    public MessageConfiguration(FileConfiguration config)
    {
        this.config = config;
    }

    public void loadConfiguration(File file) throws InvalidConfigurationException
    {
        try
        {
            this.config.load(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        ConfigurationSection section = this.config.getConfigurationSection("messages");
        for (String name : section.getKeys(true))
        {
            Object value = section.get(name);
            if (value instanceof String)
            {
                this.messages.put(name.toLowerCase(), ChatColor.translateAlternateColorCodes('&', (String) value));
            }
        }
    }

    public void addDefault(String name, String message)
    {
        if (!this.messages.containsKey(name.toLowerCase()))
        {
            return;
        }
        this.messages.put(name.toLowerCase(), message);
    }

    public String getMessage(String name)
    {
        return this.messages.get(name.toLowerCase());
    }
}
