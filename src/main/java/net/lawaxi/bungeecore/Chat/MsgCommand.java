package net.lawaxi.bungeecore.Chat;

import net.lawaxi.bungeecore.Bungeecore;
import net.lawaxi.bungeecore.Friend.FriendUtils;
import net.lawaxi.bungeecore.Player.PlayerUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MsgCommand extends Command {
    public MsgCommand() {
        super("msg",null,"tell","m");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length==0)
        {
            sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.msgFailed1"));
            return;
        }

        if(sender instanceof ProxiedPlayer){
            if(!FriendUtils.isFriend(sender.getName(),args[0]))
            {
                sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.msgFailed2").replace("%player%",args[0]));
                return;
            }
        }

        if(args.length==1)
        {
            sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.msgFailed3"));
            return;
        }

        ProxiedPlayer player = PlayerUtils.searchPlayer(args[0]);
        if(player==null)
        {
            sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.chat.common.notOnline").replace("%player%",args[0]));
            return;
        }

        ChatUtils.sendPrivateMessage(player,sender,args[1]);
    }
}
