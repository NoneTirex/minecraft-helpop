package pl.javatar.minecraft.vwhelpop.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import pl.javatar.minecraft.vwhelpop.webhook.WebHook;
import pl.javatar.minecraft.vwhelpop.webhook.WebHookListParameter;
import pl.javatar.minecraft.vwhelpop.webhook.WebHookMapParameter;
import pl.javatar.minecraft.vwhelpop.webhook.WebHookParameter;
import pl.javatar.minecraft.vwhelpop.webhook.WebHookPrimitiveParameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebHookConfiguration
{
    private final FileConfiguration          config;
    private final Map<String, List<WebHook>> webHookMap = new HashMap<>();

    public WebHookConfiguration(FileConfiguration config)
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
        ConfigurationSection section = this.config.getConfigurationSection("webhooks");
        for (String name : section.getKeys(false))
        {
            List<WebHook> webHookList = new ArrayList<>();

            List<Map<String, Object>> webHookConfigurationList = (List<Map<String, Object>>) section.getList(name);
            for (Map<String, Object> webHookConfiguration : webHookConfigurationList)
            {
                WebHook webHook = this.parseWebHook(webHookConfiguration);
                webHookList.add(webHook);
            }
            if (webHookList.size() > 0)
            {
                this.webHookMap.put(name.toLowerCase(), webHookList);
            }
        }
    }

    public Map<String, List<WebHook>> getWebHookMap()
    {
        return webHookMap;
    }

    public List<WebHook> getWebHook(String service)
    {
        return this.webHookMap.get(service.toLowerCase());
    }

    private WebHook parseWebHook(Map<String, Object> configuration)
    {
        String url = (String) configuration.get("url");
        Map<String, Object> parametersSection = (Map<String, Object>) configuration.get("parameters");
        WebHook webHook = new WebHook(url);
        webHook.setParameters(this.parseParameters(parametersSection));
        return webHook;
    }

    private WebHookParameter parseParameters(Object object)
    {
        if (object instanceof Iterable)
        {
            List<WebHookParameter> objects = new ArrayList<>();
            ((Iterable<Object>) object).forEach(element ->
            {
                objects.add(this.parseParameters(element));
            });
            return new WebHookListParameter(objects);
        }
        if (object instanceof Map)
        {
            Map<String, WebHookParameter> mapObjects = new HashMap<>();
            ((Map<String, Object>) object).forEach((key, value) ->
            {
                mapObjects.put(key, this.parseParameters(value));
            });
            return new WebHookMapParameter(mapObjects);
        }
        return new WebHookPrimitiveParameter(object);
    }

}
