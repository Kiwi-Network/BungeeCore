package net.lawaxi.bungeecore.Friend;

public class FriendUtils {

    public static boolean isFriend(String a,String  b){

        if(FriendConfig.getPlayerFriends(a).contains(b))
            return true;
        return false;
    }
}
