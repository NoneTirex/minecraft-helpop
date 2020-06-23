package pl.javatar.minecraft.vwhelpop.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.javatar.minecraft.vwhelpop.HelpopPlugin;

public class PlayerChatListener
        implements Listener
{
    private final HelpopPlugin plugin;

    public PlayerChatListener(HelpopPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();

        this.plugin.sendWebHookIfExists("chat", text ->
        {
            text = text.replace("{player_name}", player.getName());
            text = text.replace("{message}", event.getMessage());
            return text;
        });
    }
}
