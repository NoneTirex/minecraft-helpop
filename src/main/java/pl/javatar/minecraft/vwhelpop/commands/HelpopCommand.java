package pl.javatar.minecraft.vwhelpop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.goxy.minecraft.pubsub.PubSub;
import pl.javatar.minecraft.vwhelpop.HelpopPlugin;

import java.util.StringJoiner;
import java.util.logging.Level;

public class HelpopCommand
        implements CommandExecutor
{
    private final HelpopPlugin plugin;
    private final PubSub pubSub;

    public HelpopCommand(HelpopPlugin plugin, PubSub pubSub)
    {
        this.plugin = plugin;
        this.pubSub = pubSub;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length <= 0)
        {
            sender.sendMessage(this.plugin.getMessageConfiguration().getMessage("commands.helpop.correct_use"));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("on") && sender.hasPermission("helpop.toggle"))
        {
            this.plugin.getHelpopConfiguration().setEnabled(true);
            this.plugin.saveConfiguration();
            sender.sendMessage(this.plugin.getMessageConfiguration().getMessage("commands.helpop.toggle.enable"));
            return true;
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("off") && sender.hasPermission("helpop.toggle"))
        {
            this.plugin.getHelpopConfiguration().setEnabled(false);
            this.plugin.saveConfiguration();
            sender.sendMessage(this.plugin.getMessageConfiguration().getMessage("commands.helpop.toggle.disable"));
            return true;
        }

        if (!this.plugin.getHelpopConfiguration().isEnabled())
        {
            sender.sendMessage(this.plugin.getMessageConfiguration().getMessage("commands.helpop.disabled"));
            return true;
        }
        StringJoiner joiner = new StringJoiner(" ");
        for (String arg : args)
        {
            joiner.add(arg);
        }
        String message = joiner.toString();

        String playerName = sender.getName();

        HelpopData data = new HelpopData();
        data.name = playerName;
        data.message = message;
        if (this.pubSub != null)
        {
            this.pubSub.sendPluginNetwork("helpop", data).whenComplete((x, throwable) ->
            {
                if (throwable != null)
                {
                    this.plugin.getLogger()
                            .log(Level.SEVERE, "An error occurred while send helpop redis message", throwable);
                }
            });
        }
        else
        {
            this.handleMessage(data, null);
        }
        this.plugin.sendWebHookIfExists("helpop", text ->
        {
            text = text.replace("{sender_name}", playerName);
            text = text.replace("{message}", message);
            return text;
        });

        String senderMessage = this.plugin.getMessageConfiguration().getMessage("commands.helpop.message_style.sender");
        senderMessage = senderMessage.replace("{sender_name}", playerName);
        senderMessage = senderMessage.replace("{message}", message);
        sender.sendMessage(senderMessage);
        return true;
    }

    public void handleMessage(HelpopData data, String serverName)
    {
        String playerName = data.getName();
        String message = data.getMessage();

        String receiverMessage;
        if (serverName != null)
        {
            receiverMessage = this.plugin.getMessageConfiguration()
                    .getMessage("commands.helpop.message_style.receiver_server")
                    .replace("{server_name}", serverName);
        }
        else
        {
            receiverMessage = this.plugin.getMessageConfiguration()
                    .getMessage("commands.helpop.message_style.receiver");
        }
        receiverMessage = receiverMessage.replace("{sender_name}", playerName);
        receiverMessage = receiverMessage.replace("{message}", message);

        this.plugin.getServer().getConsoleSender().sendMessage(receiverMessage);
        this.plugin.getServer().broadcast(receiverMessage, "helpop.admin");
    }

    public static class HelpopData
    {
        String name;
        String message;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }
    }
}
