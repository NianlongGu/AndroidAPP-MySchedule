package com.study.shcedule.schedule;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dragon on 07.03.18.
 */


abstract class MyOnTouchListener implements View.OnTouchListener {

    Bundle bundle;
    int downTouchX,downTouchY;
    public MyOnTouchListener(Bundle outbundle) {
        this.bundle = outbundle ;
    }

}
