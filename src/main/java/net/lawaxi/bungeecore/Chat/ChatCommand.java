package net.lawaxi.bungeecore.Chat;

import net.lawaxi.bungeecore.Bungeecore;
import net.lawaxi.bungeecore.Party.PartyUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ChatCommand extends Command {
    public ChatCommand() {
        super("chat",null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        if(args.length>0){
            if(args[0].equalsIgnoreCase("p") || args[0].equalsIgnoreCase("party"))
            {
                if(PartyUtils.getParty((ProxiedPlayer) sender)!=0) {
                    ChatUtils.chatModes.replace((ProxiedPlayer) sender, ChatMode.PARTY);
                    sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.changed1"));
                }
                else
                    sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.outParty1"));
                return;
            }


            else if(args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("all")) {
                ChatUtils.chatModes.replace((ProxiedPlayer) sender, ChatMode.PUBLIC);
                sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.changed2"));
                return;
            }
        }

        sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.help1"));
        sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.help2").replace("%channel%",ChatUtils.chatModes.get((ProxiedPlayer) sender)==ChatMode.PARTY ? "party" : "all"));
    }
}
