package net.lawaxi.bungeecore.Chat;

import net.lawaxi.bungeecore.Bungeecore;
import net.lawaxi.bungeecore.Party.PartyUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

public class ChatUtils {

    public static HashMap<ProxiedPlayer,ChatMode> chatModes = new HashMap<>();

    public static void sendPartyMessage(ProxiedPlayer player,String message){

        for(ProxiedPlayer player1 : PartyUtils.playersParty.get(player).players){
            player1.sendMessage(Bungeecore.lang.getPlayerString(player1.getName(),"BungeeCore.chat.format1")
                    .replace("%message%",message)
                    .replace("%player%",player.getName()));
        }
    }

    public static void sendPrivateMessage(ProxiedPlayer to, CommandSender from, String message){

        to.sendMessage(
                Bungeecore.lang.getPlayerString(to.getName(),"BungeeCore.chat.format2")
                        .replace("%from%",from.getName())
                        .replace("%message%",message)
        );

        from.sendMessage(Bungeecore.lang.getPlayerString(from.getName(),"BungeeCore.chat.format3")
                .replace("%to%",to.getName())
                .replace("%message%",message)
        );
    }
}
