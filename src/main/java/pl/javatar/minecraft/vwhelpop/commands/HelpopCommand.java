package pl.javatar.minecraft.vwhelpop.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.javatar.minecraft.vwhelpop.HelpopPlugin;
import pl.javatar.minecraft.vwhelpop.webhook.WebHook;

import java.util.List;

public class HelpopCommand
        implements CommandExecutor
{
    private final HelpopPlugin plugin;

    public HelpopCommand(HelpopPlugin plugin)
    {
        this.plugin = plugin;
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


        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++)
        {
            sb.append(args[i]);
            if (i < args.length - 1)
            {
                sb.append(' ');
            }
        }
        String message = sb.toString();

        String receiverMessage = this.plugin.getMessageConfiguration().getMessage("commands.helpop.message_style.receiver");
        String senderMessage = this.plugin.getMessageConfiguration().getMessage("commands.helpop.message_style.sender");

        receiverMessage = receiverMessage.replace("{sender_name}", sender.getName());
        receiverMessage = receiverMessage.replace("{message}", message);
        senderMessage = senderMessage.replace("{sender_name}", sender.getName());
        senderMessage = senderMessage.replace("{message}", message);

        Bukkit.getConsoleSender().sendMessage(receiverMessage);
        Bukkit.broadcast(receiverMessage, "helpop.admin");
        this.plugin.sendWebHookIfExists("helpop", text ->
        {
            text = text.replace("{sender_name}", sender.getName());
            text = text.replace("{message}", message);
            return text;
        });
        sender.sendMessage(senderMessage);
        return true;
    }
}
