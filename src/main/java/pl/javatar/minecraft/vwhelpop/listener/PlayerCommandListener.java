package pl.javatar.minecraft.vwhelpop.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.javatar.minecraft.vwhelpop.HelpopPlugin;

public class PlayerCommandListener
        implements Listener
{
    private final HelpopPlugin plugin;

    public PlayerCommandListener(HelpopPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();

        this.plugin.sendWebHookIfExists("command", text ->
        {
            text = text.replace("{player_name}", player.getName());
            text = text.replace("{command}", event.getMessage());
            return text;
        });
    }
}
