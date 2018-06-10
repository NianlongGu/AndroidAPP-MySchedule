package com.study.shcedule.schedule;

import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Data {
    public static ArrayList<Integer> scheduleColorList;
    public static ArrayList<Bundle> bundleList;
    static {
        scheduleColorList=new ArrayList<Integer>(Arrays.asList(R.color.ink1,R.color.ink2,R.color.ink3,R.color.ink4,
                R.color.ink5,R.color.ink6,R.color.ink7,R.color.ink8,R.color.ink9,R.color.ink10,R.color.ink11,R.color.ink12,
                R.color.ink13,R.color.ink14,R.color.ink15,R.color.ink16,R.color.ink17,R.color.ink18));

        bundleList=new ArrayList<Bundle>();
//        bundleList.add(createbundle(1,490,1050,1,"主题","位置","细节"));
//        bundleList.add(createbundle(6,320,400,3,"主题","位置","细节"));
//        bundleList.add(createbundle(5,90,150,2,"主题","位置","细节"));
//        bundleList.add(createbundle(6,250,350,4,"主题","位置","细节"));
    }

    public static Bundle createbundle(int weekday, int startTime, int endTime,int color, String topic, String location, String detail ){
        Bundle info = new Bundle();
        info.putInt("weekday",weekday);
        info.putInt("startTime",startTime);
        info.putInt("endTime", endTime);
        info.putString("topic", topic);
        info.putString("location",location);
        info.putInt("color",  color);
        info.putString("detail", detail);
        return info;
    }

    public static boolean checkOverlapping( Bundle info, int exceptionIndex ){
        for(int i=0; i< bundleList.size(); i++){
            if( info.getInt("weekday") == bundleList.get(i).getInt("weekday") &&
                    info.getInt("startTime") == bundleList.get(i).getInt("startTime") &&
                    info.getInt("endTime") == bundleList.get(i).getInt("endTime") &&
                    i != exceptionIndex ){
                return true;
            }

        }
        return false;
    }


}
