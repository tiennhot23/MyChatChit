package com.example.mychatchit.Common;

public class Utils {
    public static String formatName(String name){
        name = name.trim().toLowerCase();
        StringBuilder builder = new StringBuilder();
        String[] res = name.split(" ");
        for(int i=0; i< res.length; i++){
            builder.append(res[i].toUpperCase().charAt(0)).append(res[i].substring(1).toLowerCase());
            builder.append(" ");
        }
        return builder.toString().trim();
    }

    public static Boolean checkNameRegex(String name){
        String regex = "^[a-zA-Z\\s]+";
        return name.matches(regex);
    }


}
