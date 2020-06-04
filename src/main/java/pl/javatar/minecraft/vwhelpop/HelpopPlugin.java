package pl.javatar.minecraft.vwhelpop;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import pl.javatar.http.HttpBuilder;
import pl.javatar.http.HttpConnection;
import pl.javatar.http.HttpContainer;
import pl.javatar.http.HttpContentType;
import pl.javatar.http.HttpMethod;
import pl.javatar.http.HttpStatusException;
import pl.javatar.minecraft.vwhelpop.configuration.HelpopConfiguration;
import pl.javatar.minecraft.vwhelpop.configuration.MessageConfiguration;
import pl.javatar.minecraft.vwhelpop.commands.HelpopCommand;
import pl.javatar.minecraft.vwhelpop.configuration.WebHookConfiguration;
import pl.javatar.minecraft.vwhelpop.webhook.WebHook;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;

public class HelpopPlugin
        extends JavaPlugin
{
    private final Executor executor = Executors.newSingleThreadExecutor();

    private HelpopConfiguration  helpopConfiguration;
    private WebHookConfiguration webHookConfiguration;
    private MessageConfiguration messageConfiguration;

    @Override
    public void onEnable()
    {
        this.helpopConfiguration = new HelpopConfiguration(this.getConfig());
        this.webHookConfiguration = new WebHookConfiguration(this.getConfig());
        this.messageConfiguration = new MessageConfiguration(this.getConfig());

        File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists())
        {
            this.saveDefaultConfig();
        }

        try
        {
            this.helpopConfiguration.loadConfiguration(file);
            this.webHookConfiguration.loadConfiguration(file);
            this.messageConfiguration.loadConfiguration(file);
        }
        catch (InvalidConfigurationException e)
        {
            this.getLogger().log(Level.WARNING, "Problem with load configuration", e);
            return;
        }

        this.getCommand("helpop").setExecutor(new HelpopCommand(this));
    }

    public void saveConfiguration()
    {
        File file = new File(this.getDataFolder(), "config.yml");
        this.helpopConfiguration.saveConfiguration(file);
    }

    public HelpopConfiguration getHelpopConfiguration()
    {
        return helpopConfiguration;
    }

    public WebHookConfiguration getWebHookConfiguration()
    {
        return webHookConfiguration;
    }

    public MessageConfiguration getMessageConfiguration()
    {
        return messageConfiguration;
    }

    public void sendWebHook(WebHook webHook, Function<String, String> function)
    {
        this.executor.execute(() ->
        {
            HttpBuilder httpBuilder = HttpBuilder.create();
            HttpConnection httpConnection = httpBuilder.buildConnection(webHook.getUrl());
            httpConnection.setHeader("User-Agent", "Helpop Minecraft (helpop-plugin, v0.1)");

            httpConnection.setMethod(HttpMethod.POST);
            httpConnection.setContentType(HttpContentType.JSON);

            if (webHook.getParameters() != null)
            {
                httpConnection.setParameter(HttpContainer.ROOT_PARAMETER, webHook.getParameters().toJson(function));
            }

            try
            {
                httpConnection.execute();
            }
            catch (HttpStatusException e)
            {
                this.getLogger().warning("Problem send message [" + e.getStatusCode() + "] " + webHook);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }
}
