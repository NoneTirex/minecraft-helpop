package pl.javatar.minecraft.vwhelpop.configuration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class HelpopConfiguration
{
    private final FileConfiguration config;

    private boolean enabled;

    public HelpopConfiguration(FileConfiguration config)
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
        this.enabled = this.config.getBoolean("helpop.enabled");
    }

    public void saveConfiguration(File file)
    {
        this.config.set("helpop.enabled", this.enabled);
        try
        {
            this.config.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
