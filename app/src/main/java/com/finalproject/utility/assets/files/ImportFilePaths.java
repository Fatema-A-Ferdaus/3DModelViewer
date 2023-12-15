package com.finalproject.utility.assets.files;

import android.content.Context;
import android.util.Log;
import com.finalproject.utility.notification.Notification;

import java.io.IOException;

public class ImportFilePaths {
    public static String[] import_file_path_list(Context context, String path) {

        String []paths;

        try {
            paths = context.getAssets().list(path);
            Notification.show_notification(context, paths[0],1);
            return paths;
        }
        catch (IOException e) {
            Log.d("ImportFilePaths",e.getMessage(),e.fillInStackTrace());
        }

        return null;
    }
}
