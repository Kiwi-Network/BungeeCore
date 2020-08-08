package net.lawaxi.bungeecore.Chat;

import net.lawaxi.bungeecore.Bungeecore;
import net.lawaxi.bungeecore.Party.PartyUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PChatCommand extends Command {

    public PChatCommand() {
        super("pchat",null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        if(args.length==0) {
            sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.noInput"));
            return;
        }

        if(!(sender instanceof ProxiedPlayer))
            return;

        if(!PartyUtils.playersParty.containsKey(sender))
        {
            sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.outParty3"));
        }
        else {
            ChatUtils.sendPartyMessage((ProxiedPlayer) sender,AChatCommand.toMessage(args));
        }
    }
}
