package com.finalproject.viewer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.finalproject.utility.loader3d.MLX3D;
import com.finalproject.utility.notification.DataNotification;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;

import static com.finalproject.utility.notification.Notification.show_notification;

public class OperationBoard {
    private Context context;
    private SceneView scene_view;
    LinearLayout gallery_holder;

    private MLX3D loader;

    private Node parent_node;
    private Node player;
    private Node []bed;

    private MutableLiveData<DataNotification> mdn; // mdn-> mutable data notification
    // dn-> data notification

    private float yaw_angle[]  = new float[]{0.5f};
    private float pitch_angle[]  = new float[]{0.5f};
    private boolean yaw_rotation_flag = true;
    private boolean pitch_rotation_flag = true;

    private float []touch_x = new float[]{0,0};
    private float []touch_y = new float[]{0,0};
    private final float MIN_DISTANCE = 1;

    private String []model_path = new String[]{"empty"};

    public OperationBoard(Context applicationContext, ModelViewer modelViewer, SceneView viewer, LinearLayout ddt) {
        context = applicationContext;
        scene_view = viewer;
        gallery_holder = ddt;

        loader = new MLX3D(context);

        parent_node = new Node();
        player = new Node();
        bed = new Node[]{ new Node()};

        this.mdn = new MutableLiveData<DataNotification>();
        data_notification_operator(modelViewer);
    }


    private void data_notification_operator(ModelViewer modelViewer) {

        mdn.observe(modelViewer,new Observer<DataNotification>() {
            @Override
            public void onChanged(DataNotification changedValue) {
                show_notification(context,"Object Loaded Successfully",0);

                if(changedValue.getNumber()==0){
                    parent_node.setLocalPosition(new Vector3(0,0,0));
                    parent_node.addChild(bed[0]);
                }
                if(changedValue.getNumber()==1){
                    parent_node.addChild(player);
                    scene_view.getScene().addChild(parent_node);
                    scene_view.getScene().getCamera().setLocalPosition(
                            new Vector3(0,0,1));

                    configure_view_controller();
                }
            }
        });
    }


    public void load_model_to_screen(String path,int type){
        // type 1= load model, type 0=bed
        model_path[0] = path;

        if(parent_node.getChildren().size()>1){
            parent_node.removeChild(player);
        }


        Thread updateUi = new Thread() {
            public void run() {
                try {
                    if(type==0){
                        handler_bed.sendEmptyMessage(0);
                    }
                    else if(type==1){
                        handler_model.sendEmptyMessage(0);
                    }
                    else if(type==2){
                        handler_model_runtime.sendEmptyMessage(0);
                    }
                }
                catch (Exception e) {
                    show_notification(context,"exception: loading all model to screen",1);
                }
            }
        };

        updateUi.start();
    }


    final Handler handler_model = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {

            loader.load_model(model_path[0],
                    player,
                    new Vector3(0,-0.08f,0f),
                    new Vector3(0.35f,0.35f,0.35f),
                    mdn,1);
        }
    };

    final Handler handler_model_runtime = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {

            loader.load_model_runtime(model_path[0],
                    player,
                    new Vector3(0,-0.08f,0f),
                    new Vector3(0.35f,0.35f,0.35f),
                    mdn,1);
        }
    };

    final Handler handler_bed = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {

            loader.load_model(model_path[0],
                    bed[0],
                    new Vector3(0,-0.05f,0),
                    new Vector3(1f,1f,1f),
                    mdn,0);
        }
    };


    private void configure_view_controller() {
        scene_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                touch_x[1] = motionEvent.getX();
                touch_y[1] = motionEvent.getY();

                float xchange = touch_x[1] - touch_x[0];
                float ychange = touch_y[1] - touch_y[0];

                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    yaw_rotation_control(xchange);
                    pitch_rotation_control(ychange);
                }

                touch_x[0] = touch_x[1];
                touch_y[0] = touch_y[1];

                return true;
            }
        });

    }


    private void pitch_rotation_control(float ychange) {
        // yaw_rotation_flag = true (previously left to right),
        // yaw_rotation_flag = false (previously right to left)

        if (ychange > 0) {
            // left to right touch

            if(!pitch_rotation_flag){
                pitch_rotation_flag = true;
                pitch_angle[0] = 360-( pitch_angle[0]+ ychange*0.5f);
            }
            else {
                pitch_angle[0] = pitch_angle[0] + ychange*0.5f;
            }

            if(pitch_angle[0]>360f){
                pitch_angle[0] = pitch_angle[0]%360;
            }

            operate_positional_rotation(pitch_angle[0],
                    player,
                    scene_view.getScene().getCamera());
        }

        else if (ychange < 0) {
            // right to left touch

            if(pitch_rotation_flag){
                pitch_rotation_flag = false;
                pitch_angle[0] = -360.0f-( pitch_angle[0]- ychange*0.5f);
            }
            else {
                pitch_angle[0] = pitch_angle[0] - ychange*0.5f ;
            }

            if(pitch_angle[0]<-360f){
                pitch_angle[0] = pitch_angle[0]%360;
            }

            operate_positional_rotation(-pitch_angle[0],
                    player,
                    scene_view.getScene().getCamera());
        }
    }

    private void yaw_rotation_control(float xchange) {
        // yaw_rotation_flag = true (previously left to right),
        // yaw_rotation_flag = false (previously right to left)

        if (xchange > 0) {
            // left to right touch

            if(!yaw_rotation_flag){
                yaw_rotation_flag = true;
                yaw_angle[0] = 360-( yaw_angle[0]+ xchange*0.5f);
            }
            else {
                yaw_angle[0] = yaw_angle[0] + xchange*0.5f;
            }

            if(yaw_angle[0]>360f){
                yaw_angle[0] = yaw_angle[0]%360;
            }

            operate_rotation(new Vector3(0, 1, 0),
                    yaw_angle[0],
                    parent_node);
        }

        else if (xchange < 0) {
            // right to left touch

            if(yaw_rotation_flag){
                yaw_rotation_flag = false;
                yaw_angle[0] = -360.0f-( yaw_angle[0]- xchange*0.5f);
            }
            else {
                yaw_angle[0] = yaw_angle[0] - xchange*0.5f ;
            }

            if(yaw_angle[0]<-360f){
                yaw_angle[0] = yaw_angle[0]%360;
            }
            operate_rotation(new Vector3(0, 1, 0),
                    -yaw_angle[0],
                    parent_node);
        }
    }


    private void operate_positional_rotation(float angle,Node centre_node,
                                             Node moving_node){

//        Vector3 vc = centre_node.getLocalPosition();
//        Vector3 vm = moving_node.getLocalPosition();
//        float distance = Math.abs(vm.x - vc.x);   // distance from the rotation axis

        // note: ARCore Scene-form does not allow to access the camera position vector
        //  so we keep track our own [ centre to object(camera) distance]
        //  here distance -> between x the axis]

        float distance = 1;
        float z = (float) (distance * Math.cos((double) Math.toRadians(angle)));
        float y = (float) (distance * Math.sin((double) Math.toRadians(angle)));

        moving_node.setLocalPosition(new Vector3(0,y,z));

        operate_rotation(new Vector3(1,0,0), -angle, moving_node);
    }



    public void operate_rotation(Vector3 axis, float angle,Node node){
        // single axis example-> x axis (1,0,0)---- y axis (0,1,0) ---- z axis (0,0,1)
        // angle in degree
        // time [0 to 1]

        Quaternion rotation = Quaternion.axisAngle(axis,angle);
        Quaternion result   = Quaternion.slerp(node.getLocalRotation(), rotation,1);
        node.setLocalRotation(result);
    }

//    public void change_material_data(){
//        Color color = new Color(android.graphics.Color.rgb(0, 0, 255));
//        player.getRenderable().getMaterial().setFloat3("baseColorTint", color);
//    }

    public Node getControlNode() {
        return parent_node;
    }

}
