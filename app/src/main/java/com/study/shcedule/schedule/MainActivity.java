package com.study.shcedule.schedule;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{


    protected int layout_width=0;
    protected int layout_height=0;
    private TableLayout scheduleTable;
    private FrameLayout mylayout;
    private ArrayList<TextView> inflatedTextViewList;
    private FrameLayout layoutUsedForDrag;
    private ArrayList<Boolean> flagMovableListForDragViews;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent= new Intent();
                intent.putExtra("purpose","Adding");
                intent.putExtra("extraInfo", (Bundle) null);
                intent.setClass(MainActivity.this, AddingActivity.class );
                startActivity(intent);

                return true;
            case R.id.action_setting:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_delete:
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleTable=findViewById(R.id.scheduleTable);

        mylayout= findViewById(R.id.back_layout);
        layoutUsedForDrag=findViewById(R.id.layout_used_for_drag);
        inflatedTextViewList=new ArrayList<TextView>();
        flagMovableListForDragViews = new ArrayList<Boolean>();

        mylayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mylayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int mywidth=mylayout.getWidth();
                int myheight= mylayout.getHeight();
                resfresh_weekly_schedule(  mylayout, Data.bundleList, mywidth, myheight);
            }
        });
        configureSchedulePanel();



    }

    @Override
    protected void onResume() {
        super.onResume();

        resfresh_weekly_schedule(  mylayout, Data.bundleList, mylayout.getWidth(), mylayout.getHeight());
        configActionBar();
        layoutUsedForDrag.setOnTouchListener(null);
    }


    private void configureSchedulePanel(){

        for (int i=0; i<14; i++){
            TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.layout_tablerow, scheduleTable,false  );
            for (int j =0; j< 7; j++){
                Button button = (Button) getLayoutInflater().inflate(R.layout.layout_button_in_tablerow, tableRow, false);
                Bundle bundle=new Bundle();
                bundle.putInt("time_index", i);
                bundle.putInt("week_index", j);
                button.setOnClickListener(new MyOnClickListener(bundle) {
                    @Override
                    public void onClick(View view) {
                        Bundle info = Data.createbundle(this.bundle.getInt("week_index"),(this.bundle.getInt("time_index")+7)*60,(this.bundle.getInt("time_index")+8)*60,0,"","","");
                        Intent intent= new Intent();
                        intent.putExtra("purpose","Adding");
                        intent.putExtra("extraInfo", info);
                        intent.setClass(MainActivity.this, AddingActivity.class );
                        startActivity(intent);
                    }
                });
                tableRow.addView(button);
            }
            scheduleTable.addView(tableRow);
        }
    }

    //In order to maintain the consistence of view and bundleList:
    //any change of bundle list should be followed by this function!
    private void resfresh_weekly_schedule( FrameLayout mylayout, ArrayList<Bundle> bundleList, int layout_width, int layout_height ){


//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int earlestTime=420;
        int latestTime=1260;

        //Must fist clear all existing view!! Otherwise there will be replicated view!
        mylayout.removeAllViews();
        inflatedTextViewList.clear();
        // Used for set movable and not movable
        flagMovableListForDragViews.clear();


        for(int i=0; i< bundleList.size(); i++) {
            Bundle info= bundleList.get(i);
            int weekday = info.getInt("weekday");
            int startTime= info.getInt("startTime");
            int endTime = info.getInt("endTime");
            String topic = info.getString("topic");
            String location = info.getString("location");
            int color= info.getInt("color");
            String detail= info.getString("detail");



            int modifiedStartTime=  (startTime-earlestTime) >=0? (startTime-earlestTime):0 ;

            int modifiedEndTime = (endTime-earlestTime)>=0? (endTime-earlestTime):0;
            modifiedEndTime= modifiedEndTime > (latestTime-earlestTime)? (latestTime-earlestTime): modifiedEndTime;
            modifiedEndTime = modifiedEndTime>modifiedStartTime? modifiedEndTime:modifiedStartTime;

            System.out.println("StartTime"+modifiedStartTime);
            System.out.println("endTime"+modifiedEndTime);

            //View myview = View.inflate(mylayout.getContext(), R.layout.schedule_text, mylayout);
            View myview= getLayoutInflater().inflate(R.layout.schedule_text, mylayout, false);
            ViewGroup.LayoutParams myparams= myview.getLayoutParams();
            myparams.width = (int)(layout_width/7.0);
            myparams.height= (int)(layout_height*(modifiedEndTime-modifiedStartTime)/(14.0*60));
            myview.setLayoutParams(myparams);
            setMargins(myview, (int)(layout_width*weekday/7) ,(int)(layout_height* ( (modifiedStartTime)/(14.0*60.0)) ),0,0);

            myview.setBackground(getResources().getDrawable( R.drawable.schedule_text_bg));
            //change background color without influencing its shape
            GradientDrawable drawable = (GradientDrawable) myview.getBackground();
            drawable.setColor(getResources().getColor(Data.scheduleColorList.get(color)));
            drawable.setAlpha(230);

            String theme= topic+"\n"+location;
            SpannableString ss_theme=  new SpannableString(theme);
            ss_theme.setSpan(new RelativeSizeSpan(0.8f), topic.length(),theme.length(), 0); // set size
            ss_theme.setSpan(new StyleSpan(Typeface.BOLD),0, topic.length(), 0  );
            ((TextView)myview).setText(ss_theme);
            ((TextView) myview).setGravity(Gravity.CENTER);
            ((TextView) myview).setTextColor(Color.parseColor("#FFFFFF"));

            //should pass the corresponding index of the bundle data
            Bundle bundle=new Bundle();
            bundle.putInt("bundle_index",i );
            myview.setOnClickListener(new MyOnClickListener(bundle) {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.putExtra("purpose","Editting");
                    intent.putExtra( "bundleIndex", this.bundle.getInt("bundle_index") );
                    intent.setClass(MainActivity.this, AddingActivity.class );
                    startActivity(intent);
                }
            });

            myview.setOnTouchListener(new MyOnTouchListener(bundle) {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    final  int X=(int) motionEvent.getRawX();
                    final int Y=(int) motionEvent.getRawY();
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            int [] dragPanelLocation = new int[2];
                            layoutUsedForDrag.getLocationInWindow(dragPanelLocation);
                            dragPanelPosX=dragPanelLocation[0];
                            dragPanelPosY=dragPanelLocation[1];
                            dragPanelWidth= layoutUsedForDrag.getMeasuredWidth();
                            dragPanelHeight=layoutUsedForDrag.getMeasuredHeight();
                            this.downTouchX=(int) motionEvent.getRawX();
                            this.downTouchY=(int) motionEvent.getRawY();

                            int [] localViewLocation=new int[2];
                            view.getLocationInWindow(localViewLocation);
                            touchPointToViewBorderX = this.downTouchX - localViewLocation[0];
                            touchPointToViewBorderY = this.downTouchY -localViewLocation[1];

                            downClickTime= Calendar.getInstance().getTimeInMillis();

                            longPressHandler=new LongPressHandler(this.bundle);
                            baseHandler.postDelayed( longPressHandler, 400 );
                            break;
                        case MotionEvent.ACTION_UP:
                            this.downTouchX=(int) motionEvent.getRawX();
                            this.downTouchY=(int) motionEvent.getRawY();
                            //This works even if bashHanlder has already handled the Runnable thing.
                            if(longPressHandler!=null){
                                baseHandler.removeCallbacks(longPressHandler);
                                longPressHandler=null;

                            }

                            if( flagMovableListForDragViews.get(this.bundle.getInt("bundle_index")) ==false  ){
                                int [] viewLocation= new int[2];
                                view.getLocationInWindow(viewLocation);
                                int viewWidth= view.getMeasuredWidth();
                                int viewHeight= view.getMeasuredHeight();
                                if ( X>viewLocation[0] && X<viewLocation[0]+viewWidth && Y>viewLocation[1] && Y<viewLocation[1]+viewHeight   ){


                                    Intent intent = new Intent();
                                    intent.putExtra("purpose","Editting");
                                    intent.putExtra( "bundleIndex", this.bundle.getInt("bundle_index") );
                                    intent.setClass(MainActivity.this, AddingActivity.class );
                                    startActivity(intent);
//                                    System.out.println( "Xx: "+ viewLocation[0]+"  Y: "+ viewLocation[1] );
                                }

                            }else{
                                int viewWidth= view.getMeasuredWidth();
                                int viewHeight= view.getMeasuredHeight();
                                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view
                                        .getLayoutParams();
                                layoutParams.leftMargin =  (int)( Math.round(layoutParams.leftMargin/(dragPanelWidth/7.0) ) *(dragPanelWidth/7.0) );
                                layoutParams.topMargin =(int)( Math.round(layoutParams.topMargin/(dragPanelHeight/14.0/4.0) ) *(dragPanelHeight/14.0/4.0) );
                                view.setLayoutParams(layoutParams);

                                int weekday= (int) Math.round(layoutParams.leftMargin/(dragPanelWidth/7.0));
                                int startTime =   (int) Math.round(   layoutParams.topMargin/( dragPanelHeight/(14.0*60.0))) + 420;
                                int endTime =  (int) Math.round(   (layoutParams.topMargin+ viewHeight )/( dragPanelHeight/(14.0*60.0))) + 420;

                                Data.bundleList.get(this.bundle.getInt("bundle_index")).putInt("weekday",weekday );
                                Data.bundleList.get(this.bundle.getInt("bundle_index")).putInt("startTime",startTime );
                                Data.bundleList.get(this.bundle.getInt("bundle_index")).putInt("endTime", endTime );


                            }

                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (isMoved(X,Y, this.downTouchX,this.downTouchY)){
                                if(longPressHandler!=null){
                                    baseHandler.removeCallbacks(longPressHandler);
                                    longPressHandler=null;
                                }
                            }

                            if( flagMovableListForDragViews.get(this.bundle.getInt("bundle_index"))  ) {
                                int viewWidth= view.getMeasuredWidth();
                                int viewHeight= view.getMeasuredHeight();
                                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view
                                        .getLayoutParams();
                                layoutParams.leftMargin =   Math.min( Math.max((X-touchPointToViewBorderX - dragPanelPosX),0), dragPanelWidth-viewWidth)  ;
                                layoutParams.topMargin = Math.min( Math.max( Y - touchPointToViewBorderY -dragPanelPosY,0), dragPanelHeight-viewHeight);
                                layoutParams.rightMargin = 0;
                                layoutParams.bottomMargin = 0;
                                view.setLayoutParams(layoutParams);
                            }

                    }


                    return true;
                }
            });

            mylayout.addView(myview);
            inflatedTextViewList.add((TextView) myview);
            flagMovableListForDragViews.add(false);

        }
    }



    private int statusBarHeight;

    public int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private void configActionBar(){
        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("课程表");
    }

    private long downClickTime;
    private Handler baseHandler =new Handler();
    private LongPressHandler longPressHandler=null;
    private int dragPanelPosX, dragPanelPosY, dragPanelWidth, dragPanelHeight;
    private int touchPointToViewBorderX, touchPointToViewBorderY;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view instanceof FrameLayout){
            layoutUsedForDrag.setOnTouchListener(null);
            for(int i=0; i<inflatedTextViewList.size(); i++ ){
                flagMovableListForDragViews.set(i,false);
                GradientDrawable drawable= (GradientDrawable) inflatedTextViewList.get(i).getBackground();
                drawable.setAlpha(230);
            }
            return true;
        }
        return false;
    }

    private  boolean isMoved(int x, int y, int origX, int origY){
        if(Math.abs(x-origX)<=15 && Math.abs(y-origY) <=15 ){
            return false;
        }
        return  true;
    }

    private class LongPressHandler implements Runnable{

        private Bundle bundle;

        public LongPressHandler(Bundle bundle){
            this.bundle=bundle;
        }

        @Override
        public void run() {
            flagMovableListForDragViews.set( this.bundle.getInt("bundle_index"),true);
            GradientDrawable drawable= (GradientDrawable) inflatedTextViewList.get(  this.bundle.getInt("bundle_index") ) .getBackground();
            drawable.setAlpha(100);
            layoutUsedForDrag.setOnTouchListener(MainActivity.this);
        }
    }
}


