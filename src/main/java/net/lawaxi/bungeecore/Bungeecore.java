package net.lawaxi.bungeecore;

import ee.winni.plugins.languages.LanguagesInterface;
import net.lawaxi.bungeecore.Chat.*;
import net.lawaxi.bungeecore.Commands.Lobby;
import net.lawaxi.bungeecore.Friend.FriendCommand;
import net.lawaxi.bungeecore.Friend.FriendConfig;
import net.lawaxi.bungeecore.Friend.FriendRequest;
import net.lawaxi.bungeecore.Party.PartyCommand;
import net.lawaxi.bungeecore.Party.PartyInvite;
import net.lawaxi.bungeecore.Party.PartyUtils;
import net.lawaxi.bungeecore.Player.Message;
import net.lawaxi.bungeecore.Player.PlayerUtils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import java.io.File;
import java.io.IOException;

public final class Bungeecore extends Plugin implements Listener {

    public static LanguagesInterface lang;

    @Override
    public void onEnable() {
        // Plugin startup logic

        lang = (LanguagesInterface) this.getProxy().getPluginManager().getPlugin("Languages");

        this.getProxy().getPluginManager().registerCommand(this, new PartyCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new FriendCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new Lobby());
        this.getProxy().getPluginManager().registerCommand(this, new MsgCommand());
        this.getProxy().getPluginManager().registerCommand(this, new ChatCommand());
        this.getProxy().getPluginManager().registerCommand(this, new AChatCommand());
        this.getProxy().getPluginManager().registerCommand(this, new PChatCommand());
        this.getProxy().getPluginManager().registerListener(this, this);

        FriendConfig.file = new File(getDataFolder(),"friends.yml");
        FriendConfig.prohibits_file = new File(getDataFolder(),"prohibits.yml");
        if(!getDataFolder().exists())
            getDataFolder().mkdir();

        if(!FriendConfig.file.exists())
        {
            try{
                FriendConfig.file.createNewFile();
            }
            catch (IOException e){}
        }
        if(!FriendConfig.prohibits_file.exists())
        {
            try{
                FriendConfig.prohibits_file.createNewFile();
            }
            catch (IOException e){}
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    public void onLog(PostLoginEvent e){

        //1.玩家个人纪录初始化
        PlayerUtils.players.add(e.getPlayer());
        ChatUtils.chatModes.put(e.getPlayer(), ChatMode.PUBLIC);

        //2.重载好友数据配置
        FriendConfig.reloadConfig();
        FriendConfig.reloadPConfig();

        //3.好友上线提示
        for(String a: FriendConfig.getPlayerFriends(e.getPlayer().getName()))
        {
            ProxiedPlayer player = PlayerUtils.searchPlayer(a);
            if(player!=null)
                player.sendMessage(lang.getPlayerString(player.getName(),"BungeeCore.friend.login").replace("%friend%",e.getPlayer().getName()));
        }
    }


    @EventHandler
    public void onQuit(PlayerDisconnectEvent e){

        //1.玩家个人记录清除
        PlayerUtils.players.remove(e.getPlayer());
        ChatUtils.chatModes.remove(e.getPlayer());

        //2.1离开队伍
        switch (PartyUtils.getParty(e.getPlayer())){
            case 1:
                PartyCommand.leave(e.getPlayer(),2);
                break;
            case 2:
                PartyCommand.leave(e.getPlayer(),1);
        }

        //2.2结束传送请求
        for(PartyInvite invite:PartyInvite.invites)
        {
            if(invite.from.equals(e.getPlayer())) {
                Message.sendLine(invite.to);
                invite.to.sendMessage(lang.getPlayerString(invite.to.getName(),"BungeeCore.party.invite.quit1").replace("%player%",e.getPlayer().getName()));
                Message.sendLine(invite.to);

                PartyInvite.invites.remove(invite);
            }
            else if(invite.to.equals(e.getPlayer())){
                Message.sendLine(invite.from);
                invite.from.sendMessage(lang.getPlayerString(invite.from.getName(),"BungeeCore.party.invite.quit2").replace("%player%",e.getPlayer().getName()));
                Message.sendLine(invite.from);

                PartyInvite.invites.remove(invite);
            }
        }

        //3结束好友请求
        for(FriendRequest request:FriendRequest.friendRequests){
            if(request.from.equals(e.getPlayer())) {
                Message.sendLine2(request.to);
                request.to.sendMessage(lang.getPlayerString(request.to.getName(),"BungeeCore.friend.request.quit1").replace("%player%",e.getPlayer().getName()));
                Message.sendLine2(request.to);

                FriendRequest.friendRequests.remove(request);
            }
            else if(request.to.equals(e.getPlayer())){
                Message.sendLine2(request.from);
                request.from.sendMessage(lang.getPlayerString(request.from.getName(),"BungeeCore.friend.request.quit2").replace("%player%",e.getPlayer().getName()));
                Message.sendLine2(request.from);

                FriendRequest.friendRequests.remove(request);
            }
        }

        //4.好友下线提示
        for(String a: FriendConfig.getPlayerFriends(e.getPlayer().getName()))
        {
            ProxiedPlayer player = PlayerUtils.searchPlayer(a);
            if(player!=null)
                player.sendMessage(lang.getPlayerString(player.getName(),"BungeeCore.friend.quit").replace("%friend%",e.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onChangeServer(ServerConnectedEvent e){

        ServerInfo a = e.getServer().getInfo();
        if(a.getName().equals("lobby"))
            return;

        if(PartyUtils.playersParty.containsKey(e.getPlayer()))
        {
            if(PartyUtils.playersParty.get(e.getPlayer()).leader.equals(e.getPlayer())) {
                for (ProxiedPlayer player : PartyUtils.playersParty.get(e.getPlayer()).players) {
                    if (!player.equals(e.getPlayer())) {
                        player.connect(a);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent e){

                                                      //输入指令也会调用这个事件 需要避免
        if(e.getSender() instanceof ProxiedPlayer && !e.getMessage().substring(0,1).equalsIgnoreCase("/")){
            if(ChatUtils.chatModes.get(e.getSender()).equals(ChatMode.PARTY)){

                ProxiedPlayer player = (ProxiedPlayer)e.getSender();

                if(!PartyUtils.playersParty.containsKey(player))
                {
                    player.sendMessage(lang.getPlayerString(player.getName(),"BungeeCore.chat.outParty2"));
                    ChatUtils.chatModes.replace(player,ChatMode.PUBLIC);
                }
                else {
                    ChatUtils.sendPartyMessage(player,e.getMessage());
                }

                e.setCancelled(true);
            }
        }
    }
}