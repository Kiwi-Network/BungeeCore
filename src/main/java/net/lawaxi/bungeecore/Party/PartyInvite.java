package net.lawaxi.bungeecore.Party;

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

public class PartyInvite {

    public ProxiedPlayer from;
    public ProxiedPlayer to;

    public PartyInvite(ProxiedPlayer from, ProxiedPlayer to) {
        this.from = from;
        this.to = to;


        PartyInvite t = this;
        //60秒自动取消
        new Timer().schedule(
                new TimerTask(){
                    @Override
                    public void run() {
                        if(invites.contains(t))
                        {
                            Message.sendLine(t.from);
                            t.from.sendMessage(Bungeecore.lang.getPlayerString(t.from.getName(),"BungeeCore.party.invite.info1").replace("%to%",t.to.getName()));
                            Message.sendLine(t.from);

                            Message.sendLine(t.to);
                            t.to.sendMessage(Bungeecore.lang.getPlayerString(t.to.getName(),"BungeeCore.party.invite.info2").replace("%from%",t.from.getName()));
                            Message.sendLine(t.to);

                            invites.remove(t);

                        }
                    }
                }
        ,60000);

    }

    public static ArrayList<PartyInvite> invites = new ArrayList<>();

    public static boolean sendInvite(ProxiedPlayer sender, ProxiedPlayer reciever){

        if(searchInvite(reciever)==-1) {
            invites.add(new PartyInvite(sender, reciever));

            Message.sendLine(reciever);
            reciever.sendMessage(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.party.invite.info3").replace("%player%",sender.getName()));

            TextComponent textComponent = new TextComponent(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.party.invite.info4"));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p accept"));
            textComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Bungeecore.lang.getPlayerString(reciever.getName(),"BungeeCore.common.commandText3")+"/p accept").create() ) );
            reciever.sendMessage(textComponent);
            Message.sendLine(reciever);

            return true;
        }
        return false;
    }

    public static boolean accept(ProxiedPlayer player){

        int index = searchInvite(player);

        if(index==-1)
        {
            player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(),"BungeeCore.party.invite.failed6"));
            return false;
        }
        else
        {
            ProxiedPlayer from = invites.get(index).from;

            if(!PartyUtils.playersParty.containsKey(from))
                PartyUtils.playersParty.put(from,new Party(from,Bungeecore.lang.getPlayerString(from.getName(),"BungeeCore.party.defaultName")));

            Party party = PartyUtils.playersParty.get(from);

            party.sendBoardMessage(Message.getLine());
            for(ProxiedPlayer player1 : party.players){
                player1.sendMessage(Bungeecore.lang.getPlayerString(player1.getName(),"BungeeCore.party.invite.success2").replace("%player%",player.getName()));
            }
            party.sendBoardMessage(Message.getLine());

            party.players.add(player);
            PartyUtils.playersParty.put(player,party);
            player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(),"BungeeCore.party.invite.success3").replace("%party%",party.name));
            invites.remove(index);
            return true;
        }
    }

    public static boolean deny(ProxiedPlayer player){

        int index = searchInvite(player);

        if(index==-1)
        {
            player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(),"BungeeCore.party.invite.failed7"));
            return false;
        }
        else
        {
            ProxiedPlayer from = invites.get(index).from;
            if(PartyUtils.playersParty.containsKey(from))
            {
                from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(),"BungeeCore.party.invite.success4").replace("%to%",player.getName()));
                player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(),"BungeeCore.party.invite.success5").replace("%from%",player.getName()));
            }
            else {
                player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(),"BungeeCore.party.invite.failed8"));
            }
            invites.remove(index);
            return true;
        }
    }

    private static int searchInvite(ProxiedPlayer player){

        for (PartyInvite invite : invites) {
            if (invite.to.equals(player))
                return invites.indexOf(invite);
        }
        return -1;
    }

}
