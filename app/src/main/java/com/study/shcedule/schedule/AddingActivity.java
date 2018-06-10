package com.study.shcedule.schedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AddingActivity extends AppCompatActivity {

    private Bundle info;
    private ArrayList weekdays;

    private String title ;
    private String topic ;
    private String location;
    private String detail;
    private String purpose;
    private int startTime, endTime, color;
    private int bundleInfoIndex;
//    private String topic, location, detail;
    private boolean flag_sunday,flag_monday, flag_tuesday, flag_wensday,flag_thursday, flag_friday, flag_saturday;
    private Button bt_sunday, bt_monday, bt_tuesday,bt_wensday,bt_thursday,bt_friday,bt_saturday;
    private LinearLayout color_panel;

    private ImageButton addTagButton;
    private RelativeLayout shadowBackground;
    private Button startTimeButton, endTimeButton;
    private TimePicker timePicker;
    private ImageButton image_bt_timepick_done, image_bt_timepick_cancel;
    private View hiddenview_color_set;
    private EditText editTextTopic,editTextLocation,editTextDetail;
    private ImageButton deleteButton;
    private LinearLayout deleteLayout;

    private ArrayList<Button> bt_list;
    private ArrayList<Boolean> flag_bt_list;
    private ArrayList<ImageButton> color_pannel_list;

    private boolean timePickerCalledByStartTime, timePickerCalledByEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);
        info= new Bundle();

        purpose="";
        color = 0;
        startTime=0;
        endTime=0;
        title="";
        topic="";
        location="";
        detail="";
        bundleInfoIndex=-1;

        flag_sunday=false;
        flag_monday=false;
        flag_tuesday=false;
        flag_wensday=false;
        flag_thursday=false;
        flag_friday=false;
        flag_saturday=false;

        timePickerCalledByStartTime=false;
        timePickerCalledByEndTime=false;

        bt_sunday= findViewById(R.id.sunday);
        bt_monday= findViewById(R.id.monday);
        bt_tuesday= findViewById(R.id.tuesday);
        bt_wensday= findViewById(R.id.wensday);
        bt_thursday= findViewById(R.id.thursday);
        bt_friday= findViewById(R.id.friday);
        bt_saturday= findViewById(R.id.saturday);

        color_panel=findViewById(R.id.color_panel);
        addTagButton=findViewById(R.id.addTagButton);
        startTimeButton=findViewById(R.id.start_time_bt);
        endTimeButton=findViewById(R.id.end_time_bt);

        editTextTopic=findViewById(R.id.edi_text_topic);
        editTextLocation=findViewById(R.id.edi_text_location);
        editTextDetail=findViewById(R.id.edi_text_detail);

        deleteButton=findViewById(R.id.button_delete_for_adding_activity);
        deleteLayout=findViewById(R.id.delete_layout_for_adding_activity);

        // This can get the root container, so we can generate view w.r.t the whole window, therefore we can overlay actionbar.
        ViewGroup vg = (ViewGroup)(getWindow().getDecorView().getRootView());
        hiddenview_color_set = getLayoutInflater().inflate(R.layout.clock_shadow, vg, false );
        hiddenview_color_set.setVisibility(View.GONE);
        vg.addView(hiddenview_color_set);

        //You should use the parent view to get the corresponding id! otherwise you will get null
        shadowBackground= hiddenview_color_set.findViewById(R.id.shadow_layout);
        timePicker=hiddenview_color_set.findViewById(R.id.time_picker);
        image_bt_timepick_done=hiddenview_color_set.findViewById(R.id.image_bt_picktime_done);
        image_bt_timepick_cancel=hiddenview_color_set.findViewById(R.id.image_bt_picktime_cancel);

        color_pannel_list= new ArrayList<ImageButton>();
        bt_list = new ArrayList<Button>(Arrays.asList(bt_sunday, bt_monday, bt_tuesday, bt_wensday, bt_thursday, bt_friday, bt_saturday));
        flag_bt_list = new ArrayList<Boolean>(Arrays.asList( flag_sunday, flag_monday, flag_tuesday, flag_wensday, flag_thursday, flag_friday, flag_saturday ) );
        weekdays= new ArrayList<Integer>();


        setDeleteButtonOnClickListener();

        setWeekButtonOnCLickListener();
        setColorPanelOnClickListener();
        setTimeSelectionOnClickListenser();
        initializeInformation();


    }

    private void setDeleteButtonOnClickListener(){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.bundleList.remove(bundleInfoIndex);
                finish();
            }
        });

    }

    private void initializeInformation(){


        int weekday=0;
        Intent intent=AddingActivity.this.getIntent();
        purpose= intent.getStringExtra("purpose");
        switch (purpose){
            case "Editting":
                title="编辑";
                bundleInfoIndex= intent.getIntExtra("bundleIndex",0);
                Bundle info=Data.bundleList.get(bundleInfoIndex);
                startTime= info.getInt("startTime");
                endTime = info.getInt("endTime");
                color= info.getInt("color");
                weekday = info.getInt("weekday");
                flag_bt_list.set(weekday, true  );
                topic = info.getString("topic");
                location = info.getString("location");
                detail= info.getString("detail");

                deleteLayout.setVisibility(View.VISIBLE);

                break;
            case "Adding":
                title="添加";
                Bundle extraInfo=intent.getBundleExtra("extraInfo");
                if(extraInfo==null){
                    break;
                }else {
                    startTime= extraInfo.getInt("startTime");
                    endTime = extraInfo.getInt("endTime");
                    color= extraInfo.getInt("color");
                    weekday = extraInfo.getInt("weekday");
                    flag_bt_list.set(weekday, true  );
                    topic = extraInfo.getString("topic");
                    location = extraInfo.getString("location");
                    detail= extraInfo.getString("detail");
                    break;
                }
            default:
                Toast.makeText(AddingActivity.this, "Unidentified purpose!", Toast.LENGTH_SHORT ).show();
                break;
        }

        freshAllColorButtons();
        editTextTopic.setText(topic);
        editTextLocation.setText(location);
        editTextDetail.setText(detail);

        String startDaytime= startTime/60 >=12 ? "下午":"上午";
        String endDaytime = endTime/60 >=12? "下午":"上午";
        startTimeButton.setText(String.format("%s %02d",startDaytime,startTime/60>12? startTime/60-12: startTime/60 )+":"+String.format("%02d",startTime%60));
        endTimeButton.setText(String.format("%s %02d",endDaytime,endTime/60>12? endTime/60-12: endTime/60 )+":"+String.format("%02d",endTime%60));

        configActionBar(title);
    }

    private boolean infoCompletenessCheck(){
        //first check weekday information
        int weekday=0;
        for (weekday=0; weekday<flag_bt_list.size() ;weekday++ ){
            if (flag_bt_list.get(weekday)){
                break;
            }
        }
        if (weekday >= flag_bt_list.size()){
            Toast.makeText(AddingActivity.this, "未选择日期！",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(endTime==startTime){
            Toast.makeText(AddingActivity.this, "结束时间与开始时间相同！",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(endTime<startTime){
            Toast.makeText(AddingActivity.this, "结束时间早于开始时间！",Toast.LENGTH_SHORT).show();
            return false;
        }

        if( editTextTopic.getText().toString().trim().equals("") ){
            Toast.makeText(AddingActivity.this, "未设置主题！",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if( hiddenview_color_set.getVisibility() == View.VISIBLE ){
            hiddenview_color_set.setVisibility(View.GONE);
            timePickerCalledByStartTime=false;
            timePickerCalledByEndTime=false;
        }else {
            super.onBackPressed();
        }
        // Do extra stuff here
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


    private void setTimeSelectionOnClickListenser(){
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setCurrentHour(startTime/60);
                timePicker.setCurrentMinute(startTime%60);
                hiddenview_color_set.setVisibility(View.VISIBLE);
                timePickerCalledByStartTime=true;
                timePickerCalledByEndTime=false;
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setCurrentHour(endTime/60);
                timePicker.setCurrentMinute(endTime%60);
                hiddenview_color_set.setVisibility(View.VISIBLE);
                timePickerCalledByEndTime=true;
                timePickerCalledByStartTime=false;
            }
        });

        shadowBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenview_color_set.setVisibility(View.GONE);
                timePickerCalledByStartTime=false;
                timePickerCalledByEndTime=false;
            }
        });

        image_bt_timepick_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenview_color_set.setVisibility(View.GONE);
                timePickerCalledByStartTime=false;
                timePickerCalledByEndTime=false;
            }
        });

        image_bt_timepick_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenview_color_set.setVisibility(View.GONE);
                int hour=  timePicker.getCurrentHour();
                int minute= timePicker.getCurrentMinute();

                if(timePickerCalledByStartTime){
                    startTime= hour*60+minute;
                    String startDaytime= startTime/60 >=12 ? "下午":"上午";
                    startTimeButton.setText(String.format("%s %02d",startDaytime,startTime/60>12? startTime/60-12: startTime/60 )+":"+String.format("%02d",startTime%60));
                }else if(timePickerCalledByEndTime){
                    endTime= hour*60+minute;
                    String endDaytime = endTime/60 >=12? "下午":"上午";
                    endTimeButton.setText(String.format("%s %02d",endDaytime,endTime/60>12? endTime/60-12: endTime/60 )+":"+String.format("%02d",endTime%60));
                }

                timePickerCalledByStartTime=false;
                timePickerCalledByEndTime=false;
            }
        });
    }

    private void setColorPanelOnClickListener(){

        for(int i=0; i<Data.scheduleColorList.size(); i++){
            ImageButton color_view = (ImageButton) getLayoutInflater().inflate(R.layout.color_in_panel, color_panel ,false);
            color_pannel_list.add(color_view);

            int color_id = Data.scheduleColorList.get(i);
            GradientDrawable drawable = (GradientDrawable) color_view.getBackground();
            drawable.setColor(getResources().getColor(color_id));


            if(color != i) {
                color_view.setAlpha(0);
            }else{
                color_view.setAlpha(255);
            }

            Bundle bundle=new Bundle();
            bundle.putInt("color_index",i);
            color_view.setOnClickListener(new MyOnClickListener(bundle){

                @Override
                public void onClick(View view) {

                    color = this.bundle.getInt("color_index");
                    for (int i = 0; i< color_pannel_list.size(); i++ ){
                        if(i != this.bundle.getInt("color_index")){
                            color_pannel_list.get(i).setAlpha(0);
                        }else{
                            color_pannel_list.get(i).setAlpha(255);
                        }

                    }

                    freshAllColorButtons();
                }
            });

            color_panel.addView(color_view);

        }

    }

    private  void setWeekButtonOnCLickListener(){

        for(int i =0; i<7; i++ ){
            Bundle bundle=new Bundle();
            bundle.putInt("week_index",i);
            bt_list.get(i).setOnClickListener(new MyOnClickListener(bundle) {
                @Override
                public void onClick(View view) {
                    flag_bt_list.set(this.bundle.getInt("week_index"), !flag_bt_list.get(this.bundle.getInt("week_index")));
                    if( flag_bt_list.get(this.bundle.getInt("week_index")) ){
                        bt_list.get(this.bundle.getInt("week_index")).setTextColor(Color.parseColor("#FFFFFF"));
                        bt_list.get(this.bundle.getInt("week_index")).setBackground(getResources().getDrawable(R.drawable.circle_bg));
                        GradientDrawable drawable = (GradientDrawable) bt_list.get(this.bundle.getInt("week_index")).getBackground();
                        drawable.setColor(getResources().getColor(Data.scheduleColorList.get(color)));

                    } else {
                        bt_list.get(this.bundle.getInt("week_index")).setTextColor(getResources().getColor(R.color.text_color_dark));
                        bt_list.get(this.bundle.getInt("week_index")).setBackground(getResources().getDrawable(android.R.color.transparent));

                    }

                }
            });

        }

    }

    private void freshAllColorButtons(){

        for(int i =0; i<7; i++ ){

            if( flag_bt_list.get(i) ){
                bt_list.get(i).setTextColor(Color.parseColor("#FFFFFF"));
                bt_list.get(i).setBackground(getResources().getDrawable(R.drawable.circle_bg));
                GradientDrawable drawable = (GradientDrawable) bt_list.get(i).getBackground();
                drawable.setColor(getResources().getColor(Data.scheduleColorList.get(color)));
            } else {
                bt_list.get(i).setTextColor(getResources().getColor(R.color.text_color_dark));
                bt_list.get(i).setBackground(getResources().getDrawable(android.R.color.transparent));
            }

        }

        GradientDrawable bg_drawable= (GradientDrawable) addTagButton.getBackground();
        bg_drawable.setColor(getResources().getColor(Data.scheduleColorList.get(color)));

        for(int i=0; i<color_pannel_list.size(); i++) {

            if (color != i) {
                color_pannel_list.get(i).setAlpha(0);
            } else {
                color_pannel_list.get(i).setAlpha(255);
            }

        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        final LinearLayout week_layout= findViewById(R.id.week_layout);
        week_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                week_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width=week_layout.getWidth();
                int height= week_layout.getHeight();
                ViewGroup.LayoutParams myparams= week_layout.getLayoutParams();
                myparams.height = (int)(width/7.0);
                week_layout.setLayoutParams(myparams);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu_activity_adding, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (infoCompletenessCheck()==false){
                    return false;
                }
                //multiple days can be selected!
                ArrayList<Integer> weekdayList=new ArrayList<Integer>();
                for(int i=0; i< flag_bt_list.size(); i++){
                    if( flag_bt_list.get(i)){
                        weekdayList.add(i);
                    }
                }

                for (int i=0; i< weekdayList.size();i++  ) {
                    Bundle newBundle=Data.createbundle(weekdayList.get(i), startTime, endTime, color, editTextTopic.getText().toString(), editTextLocation.getText().toString(), editTextDetail.getText().toString());
                    boolean outtest=Data.checkOverlapping(newBundle,bundleInfoIndex);
                    System.out.print(purpose);
                    boolean testfls=purpose.equals("Editting");
                    System.out.println(purpose);
                    if (outtest){
                        Toast.makeText(AddingActivity.this, "与其他计划时间重叠！",Toast.LENGTH_SHORT).show();
                        return false;
                    }else{
                        Data.bundleList.add(newBundle );
                    }
                }


                if (purpose.equals("Editting")){
                    Data.bundleList.remove(bundleInfoIndex);
                    int aa=0;
                    System.out.println(aa);
                }
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void configActionBar(String title){
        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}


