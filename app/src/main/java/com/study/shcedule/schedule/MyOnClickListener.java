package com.study.shcedule.schedule;

import android.os.Bundle;
import android.view.View;

/**
 * Created by dragon on 07.03.18.
 */


abstract class MyOnClickListener implements View.OnClickListener {

    Bundle bundle;
    public MyOnClickListener(Bundle outbundle) {
        this.bundle = outbundle ;
    }

};
