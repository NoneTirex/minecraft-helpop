package pl.javatar.minecraft.vwhelpop;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.goxy.minecraft.pubsub.PubSub;
import pl.goxy.minecraft.pubsub.PubSubService;
import pl.javatar.http.HttpBuilder;
import pl.javatar.http.HttpConnection;
import pl.javatar.http.HttpContainer;
import pl.javatar.http.HttpContentType;
import pl.javatar.http.HttpMethod;
import pl.javatar.http.HttpStatusException;
import pl.javatar.minecraft.vwhelpop.commands.HelpopCommand;
import pl.javatar.minecraft.vwhelpop.configuration.HelpopConfiguration;
import pl.javatar.minecraft.vwhelpop.configuration.MessageConfiguration;
import pl.javatar.minecraft.vwhelpop.configuration.WebHookConfiguration;
import pl.javatar.minecraft.vwhelpop.listener.PlayerChatListener;
import pl.javatar.minecraft.vwhelpop.listener.PlayerCommandListener;
import pl.javatar.minecraft.vwhelpop.webhook.WebHook;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;

public class HelpopPlugin
        extends JavaPlugin
{
    private final Executor executor = Executors.newSingleThreadExecutor();

    private HelpopConfiguration helpopConfiguration;
    private WebHookConfiguration webHookConfiguration;
    private MessageConfiguration messageConfiguration;

    @Override
    public void onEnable()
    {
        PluginManager pluginManager = this.getServer().getPluginManager();

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
        PubSub pubSub;
        if (pluginManager.isPluginEnabled("goxy-pubsub"))
        {
            PubSubService pubSubService = (PubSubService) Objects.requireNonNull(
                    pluginManager.getPlugin("goxy-pubsub"));
            pubSub = pubSubService.getPubSub(this);
        }
        else
        {
            pubSub = null;
        }
        HelpopCommand helpopCommand = new HelpopCommand(this, pubSub);
        if (pubSub != null)
        {
            pubSub.registerHandler("helpop", HelpopCommand.HelpopData.class, (context, data) ->
            {
                helpopCommand.handleMessage(data, context.getServer() != null ? context.getServer().getName() : null);
            });
        }

        pluginManager.registerEvents(new PlayerCommandListener(this), this);
        pluginManager.registerEvents(new PlayerChatListener(this), this);

        Objects.requireNonNull(this.getCommand("helpop")).setExecutor(helpopCommand);

        this.sendWebHookIfExists("server_on");
    }

    @Override
    public void onDisable()
    {
        this.sendWebHookIfExists("server_off");
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

    public void sendWebHookIfExists(String service, Function<String, String> function)
    {
        List<WebHook> webHooks = this.getWebHookConfiguration().getWebHook(service);
        if (webHooks != null)
        {
            webHooks.forEach(webHook -> this.sendWebHook(webHook, function));
        }
    }

    public void sendWebHookIfExists(String service)
    {
        this.sendWebHookIfExists(service, null);
    }
}
