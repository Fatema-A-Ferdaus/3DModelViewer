package com.finalproject.utility.compatibility;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Objects;

import static com.finalproject.utility.notification.Notification.show_notification;

public class DeviceCompatibility {
    private static final double MIN_OPENGL_VERSION = 3.0;

    public static boolean compatibility(final Activity activity) {
        String open_gl_version = ((ActivityManager)
                Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE)))
                       .getDeviceConfigurationInfo()
                       .getGlEsVersion();

        if (Double.parseDouble(open_gl_version) < MIN_OPENGL_VERSION) {
            show_notification(activity.getApplicationContext(),
                    "Sceneform does not support. \n " +
                            "requires OpenGL ES 3.0 or later", 1);
            activity.finish();
            return false;
        }

        return true;
    }
}
