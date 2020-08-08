package net.lawaxi.bungeecore.Friend;

import net.lawaxi.bungeecore.Bungeecore;
import net.lawaxi.bungeecore.Player.Message;
import net.lawaxi.bungeecore.Player.PlayerUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class FriendCommand extends Command {

    private static Bungeecore instance;

    public FriendCommand(Bungeecore instance) {

        super("friend", null, "f", "好友");
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.common.onlyPlayer"));
            return;
        }

        if (args.length == 0) {
            Message.sendLine2((ProxiedPlayer) sender);
            sendHelp((ProxiedPlayer) sender);
            Message.sendLine2((ProxiedPlayer) sender);
            return;
        }


        ProxiedPlayer ps = (ProxiedPlayer) sender;
        Message.sendLine2(ps);
        switch (args[0]) {
            default: {
                request(ps, args[0]);
                break;
            }
            case "help":{
                sendHelp(ps);
                break;
            }
            case "request":{
                if(args.length==1)
                    ps.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.friend.request.failed1"));
                else
                    request(ps, args[1]);
                break;
            }
            case "accept":{
                if(args.length==1)
                    ps.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.friend.request.failed2"));
                else{
                    ProxiedPlayer player = PlayerUtils.searchPlayer(args[1]);
                    if(player==null)
                        ps.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.friend.request.failed4").replace("%player%",args[1]));
                    else
                        FriendRequest.accept(player,ps);
                }

                break;
            }
            case "deny":{
                if(args.length==1)
                    ps.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.friend.request.failed3"));
                else{
                    ProxiedPlayer player = PlayerUtils.searchPlayer(args[1]);
                    if(player==null)
                        ps.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.common.notOnline").replace("%player%",args[1]));
                    else
                        FriendRequest.deny(player,ps);
                }

                break;
            }
            case "list":{
                String  list ="";
                for(String friend:FriendConfig.getPlayerFriends(ps.getName()))
                {
                    list+="§7 "+friend;
                    if(PlayerUtils.searchPlayer(friend)!=null)
                        list+=Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.friend.list.Online");
                    else
                        list+=Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.friend.list.notOnline");
                }

                if(list.equals(""))
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.friend.list.null"));
                else {
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.friend.list.prefix"));
                    ps.sendMessage(list.substring(0,list.length()-2));
                }
                break;

            }
            case "remove":{
                if(args.length==1)
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.friend.delete.failed1"));
                else{
                    if(FriendConfig.getPlayerFriends(ps.getName()).contains(args[1]))
                    {
                        FriendConfig.removeFriend(ps,args[1]);

                        ProxiedPlayer player = PlayerUtils.searchPlayer(args[1]);
                        if(player!=null)
                        {
                            Message.sendLine2(player);
                            player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(),"BungeeCore.friend.delete.info1").replace("%player%",ps.getName()));
                            Message.sendLine2(player);
                        }

                        ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.friend.delete.success1").replace("%player%",args[1]));
                    }
                    else
                        ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.friend.delete.failed2").replace("%player%",args[1]));

                }

                break;
            }
        }
        Message.sendLine2(ps);
    }

    private static void sendHelp(ProxiedPlayer player) {
        for(int i=1;i<=7;i++){
            player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(), "BungeeCore.friend.help.line"+i));
        }
    }

    private static void request(ProxiedPlayer from,String to){

        if(from.getName().equalsIgnoreCase(to)) {
            from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(), "BungeeCore.friend.request.failed6"));
            return;
        }

        ProxiedPlayer player = PlayerUtils.searchPlayer(to);
        if(player==null)
        {
            from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(), "BungeeCore.friend.request.failed4").replace("%player%",to));
            return;
        }

        //先查找对方有没有向你发过


        if(FriendRequest.searchRequest(player,from)!=-1)
        {
            from.sendMessage();
            from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(), "BungeeCore.friend.request.failed7").replace("from",to));
        }

        if(FriendRequest.sendRequest(from,player))
        {
            from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(), "BungeeCore.friend.request.success1"));
        }
        else
        {
            from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(), "BungeeCore.friend.request.failed8"));
        }

    }
}
