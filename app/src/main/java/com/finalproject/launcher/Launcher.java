package com.finalproject.launcher;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.finalproject.R;
import com.finalproject.viewer.ModelViewer;

import static com.finalproject.utility.activity.ActivityShifter.shift_activity;
import static com.finalproject.utility.notification.Notification.show_notification;

public class Launcher extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.launcher_activity);

            show_notification(getApplicationContext(),
                    "preparing App",
                    0);

            System.out.print("app is running");

            TextView view = findViewById(R.id.tv);

            shift_activity(this, ModelViewer.class);
        }
        catch (Exception e){
            show_notification(getApplicationContext(),
                    "Failed to Launch or Maintain App ->" +
                            " at Launcher : "+ "\n "+e.getMessage(),
                    1);
            Log.d("Launcher",e.getMessage(),e.fillInStackTrace());
        }
    }
}
