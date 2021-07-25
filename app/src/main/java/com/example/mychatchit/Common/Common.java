package com.example.mychatchit.Common;

import com.example.mychatchit.Model.UserModel;

import java.util.Random;

public class Common {
    public static UserModel currentUser = new UserModel();
    public static UserModel chatUser = new UserModel();
    public static String roomSelected = "";
    public static String translateText = "";

    public static final String USER_REFERENCES = "People";
    public static final String CHAT_LIST_REFERENCES = "ChatList";
    public static final String ROOM_CHAT_REFERENCE = "RoomChat";
    public static final String CHAT_DETAIL_REFERENCE = "Detail";
    public static final String NOTI_CONTENT = "content";
    public static final String NOTI_ROOM_ID = "room_id";
    public static final String NOTI_SENDER = "sender";
    public static final String NOTI_TITLE = "title";

    public static final int MY_CAMERA_REQUEST_CODE = 2;
    public static final int MY_RESULT_LOAD_IMAGE = 3;
    public static final int LOGIN_REQUEST_CODE = 1;

    public static String generateChatRoomId(String a, String b){
        if(a.compareTo(b) > 0){
            return new StringBuilder(a).append(b).toString();
        }else if(a.compareTo(b) < 0){
            return new StringBuilder(b).append(a).toString();
        }else{
            return new StringBuilder("Chat_Your_Self_Error").append(new Random().nextInt()).toString();
        }
    }
}
