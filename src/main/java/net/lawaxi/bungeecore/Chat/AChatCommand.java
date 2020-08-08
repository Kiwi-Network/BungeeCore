package net.lawaxi.bungeecore.Chat;

import net.lawaxi.bungeecore.Bungeecore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AChatCommand extends Command {

    public AChatCommand() {
        super("achat",null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length==0) {
            sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.noInput"));
            return;
        }

        if(!(sender instanceof ProxiedPlayer))
            return;


        boolean party = false;
        if(ChatUtils.chatModes.get(sender)==ChatMode.PARTY)
        {
            ChatUtils.chatModes.replace((ProxiedPlayer) sender,ChatMode.PUBLIC);
            party = true;
        }

        ((ProxiedPlayer)sender).chat(toMessage(args));

        if(party)
            ChatUtils.chatModes.replace((ProxiedPlayer) sender,ChatMode.PARTY);
    }

    public static String toMessage(String[] args){
        String a="";
        for(String a1:args)
            a+=a1+" ";

        return a;
    }
}
