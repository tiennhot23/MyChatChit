package com.example.mychatchit.Common;

import com.example.mychatchit.Model.UserModel;

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
}
