package net.lawaxi.bungeecore.Party;

import net.lawaxi.bungeecore.Bungeecore;
import net.lawaxi.bungeecore.Player.Message;
import net.lawaxi.bungeecore.Player.PlayerUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class PartyCommand extends Command {

    private static Bungeecore instance;

    public PartyCommand(Bungeecore instance) {

        super("party",null,"p","组队");
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!( sender instanceof ProxiedPlayer))
        {
            sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.common.onlyPlayer"));
            return;
        }

        if(args.length==0) {
            Message.sendLine((ProxiedPlayer) sender);
            sendHelp((ProxiedPlayer) sender);
            Message.sendLine((ProxiedPlayer) sender);
            return;
        }


        ProxiedPlayer ps = (ProxiedPlayer) sender;
        Message.sendLine(ps);
        switch (args[0]){
            default:{
                invite(ps,args[0]);
                break;
            }
            case "help":
            {
                sendHelp(ps);
                break;
            }
            case "invite":{
                if(args.length>1){
                    invite(ps,args[1]);
                }
                else
                    ps.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.party.invite.failed1"));
                break;
            }
            case "leave":{

                switch (PartyUtils.getParty(ps)){
                    case 0:
                        sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.party.leave.failed1"));
                        break;
                    case 1:
                        sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.party.leave.failed2"));
                        break;
                    case 2:
                    {
                        leave(ps,1);
                        sender.sendMessage(Bungeecore.lang.getPlayerString(sender.getName(),"BungeeCore.party.leave.success1"));
                        break;
                    }
                }
                break;
            }
            case "remove":{

                if(args.length>1){
                    if(PartyUtils.getParty(ps)==1){

                        Party party = PartyUtils.playersParty.get(ps);
                        boolean success = false;
                        for(ProxiedPlayer player : party.players){
                            if(player.getName().equals(args[1])){
                                party.players.remove(player);
                                PartyUtils.playersParty.remove(player);
                                player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(),"BungeeCore.party.remove.success1").replace("%leader%",ps.getName()));

                                for(ProxiedPlayer player1 : party.players){
                                    player1.sendMessage(Bungeecore.lang.getPlayerString(player1.getName(),"BungeeCore.party.remove.success2").replace("%player%",player.getName()).replace("%leader%",ps.getName()));
                                }
                                success = true;
                                break;
                            }
                        }
                        if(!success)
                            ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.remove.failed1").replace("%player%",args[1]));
                    }
                    else
                    {
                        ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.notLeader"));
                    }
                }
                else
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.remove.failed4"));
                break;
            }
            case "setname":{
                if(args.length>1){
                    if(PartyUtils.getParty(ps)==1){
                        Party party =  PartyUtils.playersParty.get(ps);

                        if(party.name.startsWith("§"))
                            party.name = party.name.substring(0,2)+args[1];
                        else
                            party.name = args[1];

                        party.sendBoardMessage(Message.getLine(),ps);
                        for(ProxiedPlayer player1 : party.players){
                            player1.sendMessage(Bungeecore.lang.getPlayerString(player1.getName(),"BungeeCore.party.setname.success1")
                                    .replace("%leader%",ps.getName())
                                    .replace("%name%",party.name));
                        }
                        party.sendBoardMessage(Message.getLine(),ps);
                    }
                    else
                    {
                        ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.notLeader"));
                    }
                }
                else
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.setname.failed1"));
                break;
            }
            case "setcolor":{
                if(args.length>1){
                    if(PartyUtils.getParty(ps)==1){
                        Party party =  PartyUtils.playersParty.get(ps);

                        if(party.name.startsWith("§")){
                            party.name = "§"+Color.getColor(args[1])+party.name.substring(2);
                        }
                        else {
                            party.name = "§"+Color.getColor(args[1])+party.name;
                        }


                        party.sendBoardMessage(Message.getLine(),ps);

                        for(ProxiedPlayer player1 : party.players){
                            player1.sendMessage(Bungeecore.lang.getPlayerString(player1.getName(),"BungeeCore.party.setname.success1")
                                    .replace("%leader%",ps.getName())
                                    .replace("%name%",party.name));
                        }
                        party.sendBoardMessage(Message.getLine(),ps);
                    }
                    else
                    {
                        ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.notLeader"));
                    }
                }
                else {
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.setname.help"));
                }
                break;
            }
            case "disband":
            {
                if(PartyUtils.getParty(ps)==1){
                    leave(ps,2);
                }
                else {
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.notLeader"));
                }
                break;
            }
            case "accept":{
                PartyInvite.accept(ps);
                break;
            }
            case "deny":{
                PartyInvite.deny(ps);
                break;
            }
            case "list":{
                if(PartyUtils.getParty(ps)!=0){
                    sendList(ps,PartyUtils.playersParty.get(ps));
                }
                else
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.outParty"));
                break;
            }
            case "warp":{
                if(PartyUtils.getParty(ps)==2){
                    ServerInfo a =PartyUtils.playersParty.get(ps).leader.getServer().getInfo();
                    ps.connect(a);
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.teleport").replace("%server%",a.getName()));
                }
                else
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.notLeader"));
                break;
            }
            case "tphere":{
                if(PartyUtils.getParty(ps)!=1)
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.notLeader"));
                else
                {
                    boolean is = false;
                    for(ProxiedPlayer player : PartyUtils.playersParty.get(ps).players)
                    {
                        if(!player.getServer().getInfo().equals(ps.getServer().getInfo())) {

                            player.connect(ps.getServer().getInfo());
                            ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.teleport1")
                                    .replace("%player%",player.getName())
                                    .replace("%server%",ps.getServer().getInfo().getName()));

                            Message.sendLine(player);
                            player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(),"BungeeCore.party.teleport2")
                                    .replace("%server%",ps.getServer().getInfo().getName()));
                            Message.sendLine(player);

                            is=true;
                        }
                    }

                    if(!is)
                        ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.teleport3")
                                .replace("%server%",ps.getServer().getInfo().getName()));
                }
                break;

            }
            case "changeleader":
            {
                if(PartyUtils.getParty(ps)!=1)
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.notLeader"));
                else
                {
                    if(args.length==1)
                        ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.change.failed1"));
                    else
                    {
                        if(args[1].equalsIgnoreCase(ps.getName()))
                            ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.change.failed2"));
                        else
                        {
                            ProxiedPlayer player = PlayerUtils.searchPlayer(args[1]);
                            if(player!=null)
                            {
                                Party party = PartyUtils.playersParty.get(ps);
                                if(party.players.contains(player))
                                {
                                    party.leader = player;
                                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.change.success1").replace("%player%",args[1]));

                                    party.sendBoardMessage(Message.getLine(),ps);

                                    for(ProxiedPlayer player1 : party.players){
                                        if(!player1.equals(ps)) {
                                            player1.sendMessages(Message.getLine());
                                            player1.sendMessage(Bungeecore.lang.getPlayerString(player1.getName(), "BungeeCore.party.change.success2").replace("%player%", args[1]));
                                        }
                                    }

                                    party.sendBoardMessage(Message.getLine(),ps);

                                    break;
                                }
                            }


                            ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.change.failed3"));
                        }
                    }
                }
                break;
            }
        }
        Message.sendLine(ps);

    }

    private static void invite(ProxiedPlayer ps,String pr1){
        if(pr1.equalsIgnoreCase(ps.getName()))
            ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.invite.failed2"));
        else
        {
            ProxiedPlayer pr = PlayerUtils.searchPlayer(pr1);
            if(pr!=null){

                if(PartyUtils.playersParty.containsKey(ps)) {
                    if(!PartyUtils.playersParty.get(ps).leader.equals(ps))
                    {
                        ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.outLeader"));
                        return;
                    }
                }
                else if(PartyUtils.playersParty.containsKey(pr))
                {
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.invite.failed3"));
                    return;
                }

                if(PartyInvite.sendInvite(ps, pr))
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.invite.success1"));
                else
                    ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.party.invite.failed4"));
            }
            else
            {
                ps.sendMessage(Bungeecore.lang.getPlayerString(ps.getName(),"BungeeCore.common.notOnline").replace("%player%",pr1));
            }

        }
    }

    public static void leave(ProxiedPlayer ps,int mode)
    {
        //mode=1 离开 mode=2 解散
        if(mode==1){
            Party party = PartyUtils.playersParty.get(ps);
            party.players.remove(ps);
            PartyUtils.playersParty.remove(ps);

            party.sendBoardMessage(Message.getLine());
            for(ProxiedPlayer player1 : party.players){
                player1.sendMessage(Bungeecore.lang.getPlayerString(player1.getName(),"BungeeCore.leave.success2").replace("%player%",ps.getName()));
            }
            party.sendBoardMessage(Message.getLine());


        }
        else {
            Party party = PartyUtils.playersParty.get(ps);

            party.sendBoardMessage(Message.getLine());
            for(ProxiedPlayer player1 : party.players){
                player1.sendMessage(Bungeecore.lang.getPlayerString(player1.getName(),"BungeeCore.leave.success3").replace("%player%",ps.getName()));
                PartyUtils.playersParty.remove(player1);
            }
            party.sendBoardMessage(Message.getLine());

            party.players = null;
        }


        for(PartyInvite invite:PartyInvite.invites)
        {
            if(invite.from.equals(ps))
            {
                Message.sendLine(invite.to);
                invite.to.sendMessage(Bungeecore.lang.getPlayerString(invite.to.getName(),"BungeeCore.invite.failed5"));
                Message.sendLine(invite.to);

                PartyInvite.invites.remove(invite);
            }
        }
    }


    private static void sendHelp(ProxiedPlayer player){
        for(int i=1;i<=14;i++){
            player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(), "BungeeCore.party.help.line"+i));
        }
    }

    private static void sendList(ProxiedPlayer player,Party party){
        player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(), "BungeeCore.party.list.prefix")
                .replace("%n%",String.valueOf(party.players.size()))
                .replace("%name%",party.name));
        player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(), "BungeeCore.party.list.leader").replace("%leader%",party.leader.getName()));


        String p = "";
        String split = Bungeecore.lang.getPlayerString(player.getName(), "BungeeCore.party.list.split");
        for(ProxiedPlayer p1:party.players)
        {
            p+=p1.getName()+split;
        }
        if(p.endsWith(split))
            p= p.substring(0,p.length()-split.length());

        player.sendMessage(Bungeecore.lang.getPlayerString(player.getName(), "BungeeCore.party.list.members").replace("%members%",p));
    }


}
