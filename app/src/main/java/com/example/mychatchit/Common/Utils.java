package com.example.mychatchit.Common;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

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

    /*
    * get file name: if it a picture from camera (not saved yet and dont have name) the fileuri scheme is "content"
    * */
    public static String getFileName(ContentResolver contentResolver, Uri fileUri){
        String result = null;
        if(fileUri.getScheme().equals("content")){
            Cursor cursor = null;
            cursor = contentResolver.query(fileUri, null, null, null, null);
            try {
                if(cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }finally {
                if(cursor != null) cursor.close();
            }
        }

        if(result == null){
            result = fileUri.getPath();
            int cut = result.lastIndexOf("/");
            if(cut != -1){
                result = result.substring(cut + 1);
            }
        }

        return result;
    }
}
