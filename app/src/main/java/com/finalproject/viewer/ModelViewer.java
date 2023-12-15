package com.finalproject.viewer;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.finalproject.R;
import com.finalproject.utility.assets.files.ImportFilePaths;
import com.finalproject.utility.compatibility.DeviceCompatibility;
import com.finalproject.utility.gesture.CPathRotationA;
import com.finalproject.utility.notification.Notification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Vector3;

import static com.finalproject.utility.notification.Notification.show_notification;

@RequiresApi(api = Build.VERSION_CODES.R)
public class ModelViewer extends AppCompatActivity {
    private SceneView viewer;
    private LinearLayout ddt;
    private OperationBoard ob;

    private String []permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.viewer_activity);


            show_notification(getApplicationContext(), "3D Viewer Started", 0);

            viewer = findViewById(R.id.viewer3D);
            ddt = findViewById(R.id.phloxviewer_data_list_holder);


            ActivityCompat.requestPermissions(this, permissions,5);

            viewer_3d_organiser();
            options_organiser();
            organise_gallery();
            scale_controller_organiser();
            rotation_controller_organiser();
            default_item_organiser();
            organise_menu_options();
        }
        catch (Exception e){
            show_notification(getApplicationContext(),
                    "Model 3D Viewer Error ->" +
                            " at ModelViewer: "+ "\n "+e.getMessage(),
                    1);
            Log.d("ModelViewer",e.getMessage(),e.fillInStackTrace());
            System.out.println(e.getMessage());
        }
    }

    private void default_item_organiser() {
        TextView default_obj_tv = findViewById(R.id.default_obj_at_card);


        default_obj_tv.setOnClickListener(e->{
            ob.load_model_to_screen("table/"+ ImportFilePaths.import_file_path_list(this,"table")[0],1);
        });
    }

    private void viewer_3d_organiser() {
        if(!DeviceCompatibility.compatibility(this)){
            return;
        }

        ob = new OperationBoard(getApplicationContext(),this,viewer,ddt);
        ob.load_model_to_screen("table/"+ImportFilePaths.import_file_path_list(this,"table")[0],1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            viewer.resume();
        }
        catch (CameraNotAvailableException e) {
            Notification.show_notification(this,"Camera not found : "+e.getMessage(),1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()){
                show_notification(this,"app needs all media access, check android app permission",1);
                return;
            }
        }

    }

    private void scale_controller_organiser() {
        Vector3 []scale = new Vector3[]{new Vector3(0.35f,0.35f,0.35f)};
        Button increaser = findViewById(R.id.phloxviewer_scale_control_increase);
        Button decreaser = findViewById(R.id.phloxviewer_scale_control_decrease);

        EditText input = findViewById(R.id.phloxviewer_scale_control_input);
        input.setHint(0.35+"");

        increase_or_decrease_button_event(scale,"s",increaser,input,1,0.1f,5f,0.3f);
        increase_or_decrease_button_event(scale,"s",decreaser,input,-1,0.1f,5f,0.3f);
        input_text_handler(scale,"s",input,0.1f,10f);
    }


    private void increase_or_decrease_button_event(Vector3 []vector,String role,Button button,EditText et,int type,float min,float max,float trigger){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float past_v = Float.parseFloat(et.getHint().toString());
                float new_v = 0;

                if(type==1){
                    new_v = past_v + trigger;
                }
                else if(type==-1){
                    new_v = past_v - trigger;
                }

                if(new_v>max){
                    new_v = max;
                }

                if(new_v<min){
                    new_v = min;
                }

                DecimalFormat dformat = new DecimalFormat();
                dformat.setMaximumFractionDigits(2);

                et.setHint(dformat.format(new_v)+"");
                et.setText(dformat.format(new_v)+"");

                if(role.equals("x")){
                    vector[0].x = new_v;
                    ob.getControlNode().setLocalPosition(vector[0]);
                }
                else if(role.equals("y")){
                    vector[0].y = new_v;
                    ob.getControlNode().setLocalPosition(vector[0]);
                }
                else if(role.equals("z")){
                    vector[0].z = new_v;
                    ob.getControlNode().setLocalPosition(vector[0]);
                }
                else if(role.equals("s")){
                    vector[0].x = new_v;
                    vector[0].y = new_v;
                    vector[0].z = new_v;
                    ob.getControlNode().setLocalScale(vector[0]);
                }
            }
        });
    }

    private void input_text_handler(Vector3 []vector, String role,EditText et,float min,float max){
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = et.getText().toString();

                if(s.equals("") || s.equals(".") || s.equals("-")){
                    return;
                }

                float value = Float.parseFloat(s);
                if(value>max){
                    value = max;
                }

                if(value<min){
                    value = min;
                }

                if(role.equals("x")){
                    vector[0].x = value;
                    ob.getControlNode().setLocalPosition(vector[0]);
                }
                else if(role.equals("y")){
                    vector[0].y = value;
                    ob.getControlNode().setLocalPosition(vector[0]);
                }
                else if(role.equals("z")){
                    vector[0].z = value;
                    ob.getControlNode().setLocalPosition(vector[0]);
                }
                else if(role.equals("s")){
                    vector[0].x = value;
                    vector[0].y = value;
                    vector[0].z = value;
                    ob.getControlNode().setLocalScale(vector[0]);
                }

                DecimalFormat dformat = new DecimalFormat();
                dformat.setMaximumFractionDigits(2);
                et.setHint(dformat.format(value)+"");
            }
        });

    }



    private void rotation_controller_organiser() {
        Button x_btn = findViewById(R.id.phloxviewer_rotation_x_btn);
        Button y_btn = findViewById(R.id.phloxviewer_rotation_y_btn);
        Button z_btn = findViewById(R.id.phloxviewer_rotation_z_btn);
        int []states = new int[]{1,0,0};
        x_btn.setBackgroundColor(Color.GREEN);

        View rotation_sensor = findViewById(R.id.phloxviewer_rotation_sensor_view);
        ImageView rotation_image = (ImageView) findViewById(R.id.phloxviewer_rotation_indicator);
        TextView center_tv = findViewById(R.id.phloxviewer_rotation_centre_tv);
        Vector3 []axis = new Vector3[]{new Vector3(1,0,0)};

        rotation_sensor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float newX = motionEvent.getX();
                float newY = motionEvent.getY();

                float current_angle = CPathRotationA.get_current_angle(rotation_sensor.getPivotX(),rotation_sensor.getPivotY(),newX,newY);

                rotation_image.setRotation(-1*current_angle);
                center_tv.setText((int) current_angle+"");

                ob.operate_rotation(axis[0],current_angle,ob.getControlNode());

                return true;
            }
        });

        Button []buttons = new Button[]{x_btn,y_btn,z_btn};

        rotation_state_button_event(buttons,axis,states,1,0,0,0);
        rotation_state_button_event(buttons,axis,states,0,1,0,1);
        rotation_state_button_event(buttons,axis,states,0,0,1,2);

    }


    private void rotation_state_button_event(Button[]buttons, Vector3[]axis, int []states, int x, int y, int z, int index){
        buttons[index].setOnClickListener(e->{
            axis[0] = new Vector3(x,y,z);
            int [] data = CPathRotationA.tri_state_logic_organiser(states,x,y,z);

            if(index==0){
                buttons[0].setBackgroundColor(Color.GREEN);
                buttons[1].setBackgroundColor(Color.BLACK);
                buttons[2].setBackgroundColor(Color.BLACK);
            }
            if(index==1){
                buttons[0].setBackgroundColor(Color.BLACK);
                buttons[1].setBackgroundColor(Color.GREEN);
                buttons[2].setBackgroundColor(Color.BLACK);
            }
            if(index==2){
                buttons[0].setBackgroundColor(Color.BLACK);
                buttons[1].setBackgroundColor(Color.BLACK);
                buttons[2].setBackgroundColor(Color.GREEN);
            }

        });
    }


    private void options_organiser() {
        LinearLayout scale_fab_holder = findViewById(R.id.phloxviewer_scale_pin_fab_holder);
        FloatingActionButton scale_fab = findViewById(R.id.phloxviewer_scale_pin_fab);
        TextView scale_title = findViewById(R.id.phloxviewer_scale_title);
        LinearLayout scale_target = findViewById(R.id.phloxviewer_scale_holder);
        organise_control_options(scale_fab,scale_fab_holder,scale_target,scale_title);


        LinearLayout rotate_fab_holder = findViewById(R.id.phloxviewer_rotate_pin_fab_holder);
        FloatingActionButton rotate_fab = findViewById(R.id.phloxviewer_rotate_pin_fab);
        TextView rotate_title = findViewById(R.id.phloxviewer_rotation_close_tv);
        LinearLayout rotate_target = findViewById(R.id.phloxviewer_rotation_holder);
        organise_control_options(rotate_fab,rotate_fab_holder,rotate_target,rotate_title);

        organise_menu_options();
        organise_gallery();

    }


    private void organise_control_options(FloatingActionButton fab,LinearLayout holder,LinearLayout target,TextView title) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                target.setVisibility(View.VISIBLE);
                holder.setVisibility(View.GONE);
            }
        });

        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.setVisibility(View.VISIBLE);
                target.setVisibility(View.GONE);
                return true;
            }
        });
    }

    private void organise_gallery() {
        LinearLayout gallery_open_holder = findViewById(R.id.phloxviewer_gallery_open_pin_fab_holder);
        FloatingActionButton gallery_open_fab = findViewById(R.id.phloxviewer_gallery_open_pin_fab);
        LinearLayout gallery_close_holder = findViewById(R.id.phloxviewer_gallery_close_pin_fab_holder);
        FloatingActionButton gallery_close_fab = findViewById(R.id.phloxviewer_gallery_close_pin_fab);
        TextView mat_viewer = findViewById(R.id.default_mat1_at_card);

        gallery_open_fab.setOnClickListener(e->{
            gallery_close_holder.setVisibility(View.VISIBLE);
            gallery_open_holder.setVisibility(View.GONE);
            ddt.setVisibility(View.VISIBLE);
            pick_file();
        });

        gallery_close_fab.setOnClickListener(e->{
            gallery_open_holder.setVisibility(View.VISIBLE);
            gallery_close_holder.setVisibility(View.GONE);
            ddt.setVisibility(View.GONE);
        });

        mat_viewer.setOnClickListener(e->{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.putExtra(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,true);
            ActivityCompat.requestPermissions(this, permissions,5);
            startActivityIfNeeded(Intent.createChooser(intent,"select file"),5);
        });

    }
    private void pick_file(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,true);
        ActivityCompat.requestPermissions(this, permissions,1);
        startActivityIfNeeded(Intent.createChooser(intent,"select file"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode==1 && resultCode==RESULT_OK){
            Uri uri = data.getData();

            System.out.println("done uri: "+uri);
            String upath = uri.getPath();
            System.out.println("done upath: "+upath);

            System.out.println(".......Ananna......");

            String path = upath.split(":")[1];
            System.out.println(".......Ananna......");
            System.out.println("done path: "+path);

            String []parse = path.split("/");
            String file_name = parse[parse.length-1];
            System.out.println("done name: "+file_name);
            String type = file_name.split("\\.")[1];

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(!Environment.isExternalStorageManager()){
                    show_notification(this,"app needs all media access, check android app permission",1);
                    return;
                }
            }

            if(type.equals("glb")){
                String location = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS)+"/"+file_name;
                ob.load_model_to_screen(location,2);
            }
            else {
                show_notification(this,"Invalid File. Select .GLB/.glb",1);
            }
        }

        if( requestCode==2 && resultCode==RESULT_OK){
            //to-do.........recording
        }

        if( requestCode==5 && resultCode==RESULT_OK){
            ob.load_model_to_screen(data.getData().getPath().split(":")[1],2);
        }
    }


    private void organise_menu_options() {
        FloatingActionButton fb_options = findViewById(R.id.phloxviewer_options_btn);
        FloatingActionButton fb_options_close = findViewById(R.id.phloxviewer_options_close_btn);
        FloatingActionButton fb_camera = findViewById(R.id.phloxviewer_options_camera_btn);



        fb_options.setOnClickListener(e->{
            fb_options_close.setVisibility(View.VISIBLE);


            ObjectAnimator animation_02 = ObjectAnimator.ofFloat(fb_camera, "translationX", 200f);
            animation_02.setDuration(300);
            animation_02.setInterpolator(new AccelerateInterpolator());
            fb_camera.setVisibility(View.VISIBLE);
            animation_02.start();

            fb_options.setVisibility(View.GONE);

        });

        fb_options_close.setOnClickListener(e->{
            fb_options.setVisibility(View.VISIBLE);


            ObjectAnimator animation_02 = ObjectAnimator.ofFloat(fb_camera, "translationX", 0f);
            animation_02.setDuration(300);
            animation_02.setInterpolator(new AccelerateInterpolator());
            animation_02.start();

            fb_camera.setVisibility(View.GONE);
            fb_options_close.setVisibility(View.GONE);

        });

        fb_camera.setOnClickListener(e ->{
            ActivityCompat.requestPermissions(this, permissions,2);
        });
    }
}


