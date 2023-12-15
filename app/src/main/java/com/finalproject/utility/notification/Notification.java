package com.finalproject.utility.notification;

import android.content.Context;
import android.widget.Toast;

public class Notification {
    public static void show_notification(Context context, String s, int length){
        Toast.makeText(context,s,length).show();
    }
}
