package net.lawaxi.bungeecore.Friend;

import net.lawaxi.bungeecore.Bungeecore;
import net.lawaxi.bungeecore.Player.Message;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FriendRequest {

    public ProxiedPlayer from;
    public ProxiedPlayer to;

    public FriendRequest(ProxiedPlayer from, ProxiedPlayer to) {
        this.from = from;
        this.to = to;


        FriendRequest t = this;
        new Timer().schedule(
                new TimerTask(){
                    @Override
                    public void run() {
                        if(friendRequests.contains(t))
                        {
                            Message.sendLine25(t.from);
                            t.from.sendMessage(Bungeecore.lang.getPlayerString(t.from.getName(),"BungeeCore.friend.request.info1").replace("%to%",t.to.getName()));
                            Message.sendLine25(t.from);

                            Message.sendLine25(t.to);
                            t.to.sendMessage(Bungeecore.lang.getPlayerString(t.to.getName(),"BungeeCore.friend.request.info2").replace("%from%",t.from.getName()));
                            Message.sendLine25(t.to);

                            friendRequests.remove(t);
                        }
                    }
                }
                ,60000);
    }

    public static ArrayList<FriendRequest> friendRequests = new ArrayList<>();


    public static boolean sendRequest(ProxiedPlayer sender, ProxiedPlayer reciever){

        if(searchRequest(sender,reciever)==-1) {
            friendRequests.add(new FriendRequest(sender,reciever));

            Message.sendLine25(reciever);
            reciever.sendMessage(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.friend.request.info3") + sender.getName());

            TextComponent a = new TextComponent(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.friend.request.info4"));
            a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept "+sender.getName()));
            a.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.common.commandText1")+"/f accept "+sender.getName()).create() ));

            TextComponent b = new TextComponent(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.friend.request.info6"));
            b.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny "+sender.getName()));
            b.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.common.commandText2")+"/f deny "+sender.getName()).create() ));


            reciever.sendMessage(a,new TextComponent(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.friend.request.info5")),b);
            reciever.sendMessage(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.friend.request.info7"));
            Message.sendLine25(reciever);

            return true;
        }
        return false;
    }

    public static int searchRequest(ProxiedPlayer from, ProxiedPlayer to){

        for (FriendRequest request:friendRequests) {
            if (request.to.equals(to) &&request.from.equals(from))
                return friendRequests.indexOf(request);
        }
        return -1;
    }

    public static boolean accept(ProxiedPlayer from,ProxiedPlayer to){

        int index = searchRequest(from,to);

        if(index==-1)
        {
            to.sendMessage(Bungeecore.lang.getPlayerString(to.getName(),"BungeeCore.friend.request.failed5"));
            return false;
        }
        else
        {

            FriendConfig.addFriend(from,to);

            Message.sendLine2(from);
            from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(),"BungeeCore.friend.request.success2").replace("%to%",to.getName()));
            Message.sendLine2(from);

            //由于输入指令时有自动补充 sendLine2 此处无需添加
            to.sendMessage(Bungeecore.lang.getPlayerString(from.getName(),"BungeeCore.friend.request.success3").replace("%from%",from.getName()));

            friendRequests.remove(index);
            return true;
        }

    }

    public static boolean deny(ProxiedPlayer from,ProxiedPlayer to){

        int index = searchRequest(from,to);

        if(index==-1)
        {
            to.sendMessage(Bungeecore.lang.getPlayerString(to.getName(),"BungeeCore.friend.request.failed5"));
            return false;
        }
        else
        {

            Message.sendLine2(from);
            from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(),"BungeeCore.friend.request.success4").replace("%to%",to.getName()));
            Message.sendLine2(from);

            //由于输入指令时有自动补充 sendLine2 此处无需添加
            to.sendMessage(Bungeecore.lang.getPlayerString(from.getName(),"BungeeCore.friend.request.success5").replace("%from%",from.getName()));

            friendRequests.remove(index);
            return true;
        }

    }
}
