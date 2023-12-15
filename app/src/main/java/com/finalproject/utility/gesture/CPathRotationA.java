package com.finalproject.utility.gesture;

public class CPathRotationA {
    //circular path rotation detection on 2D plane view

    public static float get_rotation_angle(float past_angle, float current_angle){
        float change= current_angle - past_angle;
        return past_angle+change;
    }

    public static float get_current_angle(float centreX, float centreY, float posX, float posY){
        int result = quad_rule_processor(centreX, centreY, posX, posY);

        if(result==-1){
            return specific_rule_processor(centreX, centreY, posX, posY);
        }

        return calculate_angle(result, centreX, centreY, posX, posY);
    }

    private static float calculate_angle(int result, float centreX, float centreY, float posX, float posY) {
        float x = posX - centreX;
        float y = posY - centreY;

        x = Math.abs(x);
        y = Math.abs(y);

        float angle = (float) Math.toDegrees(Math.atan(y/x));

        if(result==2){
            return 180-angle;
        }
        else if(result==3){
            return 180+angle;
        }
        else if(result==4){
            return 360-angle;
        }

        return angle;
    }

    private static float specific_rule_processor(float centreX, float centreY, float posX, float posY) {
        //on a circle --> (R,0), (0,R), (-R,0), (0,-R)
        // R -> Radius
        if(posX-centreX==0){
           if(posY-centreY>0){
               return 270;
           }
           if(posY-centreY<0){
               return 90;
           }
        }
        if(posY-centreY==0){
            if(posX-centreX>0){
                return 0;
            }
            if(posX-centreX<0){
                return 180;
            }
        }
        return -1;
    }

    private static int quad_rule_processor(float centreX, float centreY, float posX, float posY) {
        if(posX-centreX>0){
            if(posY-centreY>0){
                return 4;
            }
            if(posY-centreY<0){
                return 1;
            }
        }
        if(posX-centreX<0){
            if(posY-centreY>0){
                return 3;
            }
            if(posY-centreY<0){
                return 2;
            }
        }
        return -1;
    }

    public static int[] tri_state_logic_organiser(int []data, int x, int y, int z){

        if (data.length==3){
            if (x==1 && y==0 && z==0){
                data[0] = 1;
                data[1] = 0;
                data[2] = 0;
            }
            if (x==0 && y==1 && z==0){
                data[0] = 0;
                data[1] = 1;
                data[2] = 0;
            }
            if (x==0 && y==0 && z==1){
                data[0] = 0;
                data[1] = 0;
                data[2] = 1;
            }
        }
        return data;
    }
}
