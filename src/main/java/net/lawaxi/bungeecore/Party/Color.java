package net.lawaxi.bungeecore.Party;

public class Color {

    public static String getColor(String input){

        switch (input){
            case "black":return "0";
            case "dark_blue":return "1";
            case "dark_green":return "2";
            case "dark_aqua":return "3";
            case "dark_red":return "4";
            case "dark_purple":return "5";
            case "gold": case "orange":return "6";
            case "gray":return "7";
            case "dark_gray":return "8";
            case "blue":return "9";
            case "green":return "a";
            case "aqua": case "cyan":return "b";
            case "red":return "c";
            case "light_purple":return "d";
            case "yellow":return "e";
            case "while":return "f";
            default:return input.substring(0,1);
        }
    }
}
