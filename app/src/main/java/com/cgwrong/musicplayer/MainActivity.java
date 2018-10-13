package com.cgwrong.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,allMusicFragment.FragmentInteraction,ViewPager.OnPageChangeListener{

    private MyFragmentPagerAdapter fragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private ViewPager viewPager;

    private RadioGroup musicListWay;
    private Button searchButton;
    private Button download;
    private Button time;
    private Button settings;
    private RadioGroup radioGroup;
    private Button exit;
    private FloatingActionButton fab;
    private EditText searchText;
    private TextView musicTitle;
    private TextView musicArtist;
    private ImageButton play_pause;
    private RadioButton allMusic;
    private RadioButton musicDirectory;
    private ImageButton previous;
    private ImageButton next;
    private SeekBar playProgress;
    private RelativeLayout toNew;

    private MsgReceiver receiver;

    private DrawerLayout mDrawerLayout;
    private LinearLayout searchLinear;

    private int position;
    private String musicUrl;
    private String musicSize;
    private String musicName;

    private RemoteViews contentViews;
    private NotificationManager notificationManager;
    private notiReceiver notireceiver;

    private MusicService.MusicBinder musicBinder;

    private ServiceConnection connection=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder=(MusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private allMusicFragment allMusicFragment;
    private MusicListFragment musicListFragment;

    private Notification notification;
    private int id=0;

    //开启新线程
    private Handler h=new Handler();
    private Runnable delayRun=new Runnable() {
        @Override
        public void run() {
            allMusicFragment.updateMusic(searchText.getText().toString().trim().toLowerCase());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager=(ViewPager) findViewById(R.id.viewpager);
        fragments=new ArrayList<Fragment>();
        fragments.add(new allMusicFragment());
        fragments.add(new MusicListFragment());
        fragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOnPageChangeListener(this);

        allMusicFragment=(allMusicFragment) fragmentPagerAdapter.getItem(0);

        //获取fragment实例
        //FragmentManager supportFragmentManager = getSupportFragmentManager();
        //allMusicFragment = (allMusicFragment) supportFragmentManager.findFragmentById(R.id.allmusic_fragment);
        //musicListFragment=(MusicListFragment) supportFragmentManager.findFragmentById(R.id.musiclist_fragment);

        //toolbar设置
        android.support.v7.widget.Toolbar toolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActivityCollector.addActivity(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.list);
        }

        searchLinear=(LinearLayout) findViewById(R.id.search_linear);
        musicListWay=(RadioGroup) findViewById(R.id.music_list_way);
        searchText=(EditText) findViewById(R.id.search_text);
        searchButton=(Button) findViewById(R.id.search_button);
        download=(Button) findViewById(R.id.download);
        time=(Button) findViewById(R.id.time);
        settings=(Button) findViewById(R.id.settings);
        radioGroup=(RadioGroup) findViewById(R.id.radio_group);
        exit=(Button) findViewById(R.id.exit);
        fab=(FloatingActionButton) findViewById(R.id.fab);
        musicTitle=(TextView) findViewById(R.id.title);
        musicArtist=(TextView) findViewById(R.id.artist);
        play_pause=(ImageButton) findViewById(R.id.pause);
        allMusic=(RadioButton) findViewById(R.id.all_music);
        musicDirectory=(RadioButton) findViewById(R.id.music_list);
        previous=(ImageButton) findViewById(R.id.previous);
        next=(ImageButton) findViewById(R.id.next);
        toNew=(RelativeLayout) findViewById(R.id.toNewframe);

        RadioButton musicAll=(RadioButton) findViewById(R.id.all_music);
        musicAll.setChecked(true);
        RadioButton randomPlay=(RadioButton) findViewById(R.id.random_play);
        randomPlay.setChecked(true);

        exit.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        download.setOnClickListener(this);
        time.setOnClickListener(this);
        settings.setOnClickListener(this);
        fab.setOnClickListener(this);
        allMusic.setOnClickListener(this);
        musicDirectory.setOnClickListener(this);
        play_pause.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        toNew.setOnClickListener(this);

        //启动、绑定服务
        Intent playIntent=new Intent(MainActivity.this,MusicService.class);
        startService(playIntent);
        bindService(playIntent,connection,BIND_AUTO_CREATE);

        //音乐列表选择
        musicListWay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.all_music:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.music_list:
                        viewPager.setCurrentItem(1);
                        break;
                    default:
                        break;
                }
            }
        });

        //歌曲搜索
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (delayRun!=null){
                    h.removeCallbacks(delayRun);
                }
                h.postDelayed(delayRun,800);
            }
        });


        playProgress=(SeekBar) findViewById(R.id.play_progress);
        playProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicBinder.seekToMusic(seekBar.getProgress());
            }
        });

        //监听器注册
        receiver=new MsgReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("MusicChange");
        registerReceiver(receiver,intentFilter);

        //初始化通知栏
        initNotification();

        //notification监听器注册
        notireceiver=new notiReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("NOTIFICATION");
        registerReceiver(notireceiver,filter);
    }

    public void initNotification(){

        contentViews=new RemoteViews(getPackageName(),R.layout.notification_layout);
        contentViews.setImageViewResource(R.id.notification_logo,R.drawable.logo);

        Intent intentNext=new Intent("NOTIFICATION");
        intentNext.putExtra("type","NEXT");
        PendingIntent pendingNext=PendingIntent.getBroadcast(this,0,intentNext,0);
        contentViews.setOnClickPendingIntent(R.id.notification_next,pendingNext);
        Intent intentPrevious=new Intent("NOTIFICATION");
        intentPrevious.putExtra("type","PREVIOUS");
        PendingIntent pendingPrevious=PendingIntent.getBroadcast(this,1,intentPrevious,0);
        contentViews.setOnClickPendingIntent(R.id.notification_previous,pendingPrevious);
        Intent intentPlay=new Intent("NOTIFICATION");
        intentPlay.putExtra("type","PLAY");
        PendingIntent pendingPlay=PendingIntent.getBroadcast(this,2,intentPlay,0);
        contentViews.setOnClickPendingIntent(R.id.notification_play_pause,pendingPlay);
        Intent intentClose=new Intent("NOTIFICATION");
        intentClose.putExtra("type","CLOSE");
        PendingIntent pendingClose=PendingIntent.getBroadcast(this,3,intentClose,0);
        contentViews.setOnClickPendingIntent(R.id.notification_close,pendingClose);

        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(MainActivity.this);
        mBuilder.setContent(contentViews)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setSmallIcon(R.drawable.logo);
        Intent intent=new Intent(MainActivity.this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pi);
        notification=mBuilder.build();
        notification.flags=Notification.FLAG_ONGOING_EVENT;
        notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id,notification);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    //toolbar点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.about:
                break;
            case R.id.donate:
                break;
        }
        return true;
    }

    //存儲雙擊時間
    long[] mHits=new long[2];
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //退出按钮
            case R.id.exit:
                ActivityCollector.finishAll();
                break;
            //回到顶部
            case R.id.all_music:
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1]= SystemClock.uptimeMillis();
                if(mHits[0]>=(SystemClock.uptimeMillis()-500)){
                    allMusicFragment.listViewToTop();
                    mHits=null;
                    mHits=new long[2];
                }
                break;
            //定位
            case R.id.fab:
                allMusicFragment.listViewSelection(position);
                break;
            //暂停和播放
            case R.id.pause:
                PlayOrPause();
                break;
            //下一首
            case R.id.next:
                next();
                break;
            //上一首
            case R.id.previous:
                previous();
                break;
            //跳转
            case R.id.toNewframe:
                Intent intent=new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("musicTitle",musicTitle.getText().toString());
                intent.putExtra("musicArtist",musicArtist.getText().toString());
                intent.putExtra("musicSize",musicSize);
                intent.putExtra("musicName",musicName);
                intent.putExtra("musicUrl",musicUrl);
                startActivity(intent);
                break;
            //搜索音乐
            case R.id.search_button:
                Intent search_intent=new Intent(this,SearchActivity.class);
                search_intent.putExtra("content",searchText.getText().toString());
                startActivity(search_intent);
                break;
            //定时
            case R.id.time:
                TimePickerDialog timePickerDialog=new TimePickerDialog(this,android.R.style.Theme_Holo_Dialog,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setTime(hourOfDay,minute);
                    }
                },0,0,true);
                timePickerDialog.setTitle("定时");
                timePickerDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Intent stopService=new Intent(this,MusicService.class);
        stopService(stopService);
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        unregisterReceiver(receiver);
        unregisterReceiver(notireceiver);
        notificationManager.cancelAll();
        ActivityCollector.removeActivity(this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //重写点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                if(hideInputMethod(this, v)) {
                    return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    public static Boolean hideInputMethod(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void process(String title,String artist,int position,String url,String size,String name){
        musicTitle.setText(title);
        musicArtist.setText(artist);
        musicSize=size;
        musicName=name;
        musicUrl=url;
        this.position=position;
        play_pause.setImageResource(R.drawable.pause);
        musicBinder.playMusic(url);
        handler.sendEmptyMessage(MUSICDURATION);
        handler.sendEmptyMessage(UPDATE);
        updateNotification();

        Intent intent=new Intent();
        intent.putExtra("musicName",name);
        intent.putExtra("musicSize",size);
        intent.putExtra("Music","change");
        intent.putExtra("musicTitle",title);
        intent.putExtra("musicArtist",artist);
        intent.putExtra("musicUrl",musicUrl);
        intent.setAction("MusicChange");
        sendBroadcast(intent);
    }

    private final int MUSICDURATION = 0x1;    //获取歌曲播放时间标志
    private final int UPDATE = 0x2;       //更新进度条标志
    //进度条设置
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case MUSICDURATION:
                    playProgress.setMax(new Long(musicBinder.getDuration()).intValue());
                    break;
                case UPDATE:
                    playProgress.setProgress(new Long(musicBinder.getCurrent()).intValue());
                    handler.sendEmptyMessageDelayed(UPDATE,500);
                    break;
            }
        }
    };

    private int mode=0;
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if(mode==1){                             //退出多选编辑模式
                allMusicFragment.callBack();
                mode=0;
            }else if(!searchText.getText().toString().trim().equals("")){         //清空搜索框
                searchText.setText("");
            }else if(mode==2){                      //返回列表
                mode=0;
                fragments.remove(1);
                fragments.add(new MusicListFragment());
                fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                viewPager.setAdapter(fragmentPagerAdapter);
                viewPager.setCurrentItem(1);
            }else{
                this.onDestroy();
                System.exit(0);
            }
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                allMusic.performClick();
                break;
            case 1:
                musicDirectory.performClick();
                break;
                default:
                    break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //广播接收器
    public class MsgReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context,Intent intent){
            switch (intent.getStringExtra("Music")){
                case "finished":
                    next();
                    break;
                case "Pause":
                    PlayOrPause();
                    break;
                case "Next":
                    next();
                    break;
                case "Previous":
                    previous();
                    break;
                case "NextPlay":
                    NextPlay(intent.getIntExtra("position",0));
                    break;
                case "Back":
                    mode=intent.getIntExtra("Mode",0);
                    break;
                default:
                    break;
            }

        }
    }

    //notification广播
    public class notiReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            String extra=intent.getStringExtra("type");
            switch (extra){
                case "PLAY":
                    PlayOrPause();
                    break;
                case "NEXT":
                    next();
                    break;
                case "PREVIOUS":
                    previous();
                    break;
                case "CLOSE":
                    MainActivity.this.onDestroy();
                    break;
            }
        }
    }

    //下一首
    public void next(){
        if(list==null||list.size()==0) {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.repeat_play:
                    musicBinder.repeatPlay();
                    handler.sendEmptyMessage(MUSICDURATION);
                    handler.sendEmptyMessage(UPDATE);
                    break;
                case R.id.random_play:
                    allMusicFragment.randomPlay();
                    position = allMusicFragment.getPosition();
                    allMusicFragment.listViewSelection(position);
                    handler.sendEmptyMessage(MUSICDURATION);
                    handler.sendEmptyMessage(UPDATE);
                    break;
                case R.id.repeat_list:
                    allMusicFragment.listrepeatPlaynext(position);
                    position = allMusicFragment.getPosition();
                    allMusicFragment.listViewSelection(position);
                    handler.sendEmptyMessage(MUSICDURATION);
                    handler.sendEmptyMessage(UPDATE);
                    break;
            }
        }else{
            allMusicFragment.nextPlay((int)list.get(0));
            list.remove(0);
            position=allMusicFragment.getPosition();
            handler.sendEmptyMessage(MUSICDURATION);
            handler.sendEmptyMessage(UPDATE);
        }
        updateNotification();
    }

    //上一首
    public void previous(){
        switch(radioGroup.getCheckedRadioButtonId()) {
            case R.id.repeat_play:
                musicBinder.repeatPlay();
                handler.sendEmptyMessage(UPDATE);
                break;
            case R.id.random_play:
                allMusicFragment.randomPlay();
                position = allMusicFragment.getPosition();
                allMusicFragment.listViewSelection(position);
                handler.sendEmptyMessage(MUSICDURATION);
                handler.sendEmptyMessage(UPDATE);
                break;
            case R.id.repeat_list:
                allMusicFragment.listrepeatPlayprevious(position);
                position = allMusicFragment.getPosition();
                allMusicFragment.listViewSelection(position);
                handler.sendEmptyMessage(MUSICDURATION);
                handler.sendEmptyMessage(UPDATE);
                break;
        }
        updateNotification();
    }

    //暂停、播放
    public void PlayOrPause(){
        if(musicBinder.isPlayMusic()){
            musicBinder.pauseMusic();
            updateNotification();
        }else if(!musicBinder.isPlayMusic()){
            musicBinder.startMusic();
            updateNotification();
        }else {
            Toast.makeText(this,"没有音乐！",Toast.LENGTH_SHORT);
        }
    }

    //更新notification
    public void updateNotification(){
        contentViews.setTextViewText(R.id.notification_title,musicTitle.getText());
        contentViews.setTextViewText(R.id.notification_artist,musicArtist.getText());
        if(musicBinder.isPlayMusic()){
            play_pause.setImageResource(R.drawable.pause);
            contentViews.setImageViewResource(R.id.notification_play_pause,R.drawable.notification_pause);
        }else if (!musicBinder.isPlayMusic()){
            play_pause.setImageResource(R.drawable.play);
            contentViews.setImageViewResource(R.id.notification_play_pause,R.drawable.notification_play);
        }
        notificationManager.notify(id,notification);
    }

    //定时
    private Timer timer;
    public void setTime(int hour,int minute){
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ActivityCollector.finishAll();
            }
        },(hour*60+minute)*60*1000);
    }

    //稍后播
    private List list;
    public void NextPlay(int  position){
        if(list==null){
            list=new ArrayList();
            list.add(position);
        }else {
            list.add(position);
        }
    }

}