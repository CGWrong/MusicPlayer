package com.cgwrong.musicplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private android.support.v7.widget.Toolbar toolbar;

    private ImageButton play;
    private ImageButton previous;
    private ImageButton next;
    private ImageButton delete;
    private ImageButton addToList;
    private ImageButton searchLrc;
    private ImageButton information;

    private String title_str;
    private String artist_str;
    private String musicSize;
    private String name;
    private String musicUrl;

    private LrcView lrcView;
    private LrcProgress lrcProgress;
    private List<LrcContent> lrcList=new ArrayList<LrcContent>();
    private int index=0;

    private MsgReceiver msgReceiver;

    private SeekBar seekBar;
    private MusicService.MusicBinder musicBinder;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActivityCollector.addActivity(this);

        Intent intent = getIntent();

        name=intent.getStringExtra("musicName");
        musicSize=intent.getStringExtra("musicSize");
        musicUrl=intent.getStringExtra("musicUrl");
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.detail_toolbar);
        title_str=intent.getStringExtra("musicTitle");
        artist_str=intent.getStringExtra("musicArtist");
        toolbar.setTitle(title_str);
        toolbar.setSubtitle(artist_str);
        setSupportActionBar(toolbar);
        //设置返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MusicChange");
        registerReceiver(msgReceiver, intentFilter);


        seekBar = (SeekBar) findViewById(R.id.datail_play_progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        play = (ImageButton) findViewById(R.id.datail_pause);
        next = (ImageButton) findViewById(R.id.datail_next);
        previous = (ImageButton) findViewById(R.id.datail_previous);
        delete = (ImageButton) findViewById(R.id.delete);
        addToList = (ImageButton) findViewById(R.id.add_to_list);
        searchLrc = (ImageButton) findViewById(R.id.search_lrc);
        information = (ImageButton) findViewById(R.id.information);

        lrcView=(LrcView) findViewById(R.id.lrcView);

        play.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        delete.setOnClickListener(this);
        addToList.setOnClickListener(this);
        searchLrc.setOnClickListener(this);
        information.setOnClickListener(this);

        Intent playIntent = new Intent(DetailActivity.this, MusicService.class);
        bindService(playIntent, connection, BIND_AUTO_CREATE);



        initLrc();
    }

    @Override
    protected void onStart(){
        super.onStart();
        handler.sendEmptyMessage(MUSICDURATION);
        handler.sendEmptyMessage(UPDATE);
    }

    private Handler mHandler=new Handler();
    public void initLrc(){
        lrcProgress = new LrcProgress();
        //读取歌词文件
        if(musicUrl!=null) {
            lrcProgress.readLRC(musicUrl);
        }
        //传回处理后的歌词文件
        lrcList = lrcProgress.getLrcList();
        lrcView.setmLrcList(lrcList);
        //切换带动画显示歌词
        //lrcView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.));
        mHandler.post(runnable);

    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            lrcView.setIndex(lrcIndex());
            lrcView.invalidate();
            mHandler.postDelayed(runnable,100);
        }
    };

    private long currentTime;
    private long duration;
    public int lrcIndex(){
        if(musicBinder.isPlayMusic()){
            currentTime=musicBinder.getCurrent();
            duration=musicBinder.getDuration();
        }
        if(currentTime<duration){
            for(int i=0;i<lrcList.size();i++){
                if(i<lrcList.size()-1){
                    if(currentTime<lrcList.get(i).getLrcTime()&&i==0)
                        index=i;
                    if(currentTime>lrcList.get(i).getLrcTime()&&currentTime<lrcList.get(i+1).getLrcTime())
                        index=i;
                }
                if (i==lrcList.size()-1&&currentTime>lrcList.get(i).getLrcTime())
                    index=i;
            }
        }
        return index;
    }

    private final int MUSICDURATION = 0x1;    //获取歌曲播放时间标志
    private final int UPDATE = 0x2;       //更新进度条标志
    //进度条设置
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(musicBinder!=null) {
                switch (msg.what) {
                    case MUSICDURATION:
                        seekBar.setMax(new Long(musicBinder.getDuration()).intValue());
                        break;
                    case UPDATE:
                        seekBar.setProgress(new Long(musicBinder.getCurrent()).intValue());
                        handler.sendEmptyMessageDelayed(UPDATE, 500);
                        break;
                }

                if (musicBinder.isPlayMusic()) {
                    play.setImageResource(R.drawable.pause);
                } else if (!musicBinder.isPlayMusic()) {
                    play.setImageResource(R.drawable.play);
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        Intent stopService = new Intent(this, MusicService.class);
        stopService(stopService);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.datail_pause:
                play_pause();
                break;
            case R.id.datail_next:
                next();
                break;
            case R.id.datail_previous:
                previous();
                break;
            case R.id.search_lrc:
                View view = View.inflate(DetailActivity.this, R.layout.lrc_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("歌词搜索");
                builder.setView(view);
                EditText title=(EditText) view.findViewById(R.id.search_title_edit);
                EditText artist=(EditText) view.findViewById(R.id.search_artist_edit);
                title.setText(title_str);
                artist.setText(artist_str);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                break;
            case R.id.information:
                MusicDialog.Builder builder1=new MusicDialog.Builder(this);
                builder1.setName(name)
                        .setSize(musicSize)
                        .setUrl(musicUrl)
                        .create()
                        .show();
                break;
            case R.id.delete:
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("提示！");
                dialog.setMessage("是否删除文件？");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File f=new File(musicUrl);
                        f.delete();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                break;
            case R.id.add_to_list:
                AddToListDialog.Builder addDialog=new AddToListDialog.Builder(this);
                addDialog.setListView();
                addDialog.create()
                        .show();
                break;
            default:
                break;
        }
    }

    public void play_pause() {
        Intent playIntent = new Intent();
        playIntent.putExtra("Music", "Pause");
        playIntent.setAction("MusicChange");
        sendBroadcast(playIntent);
        if (musicBinder.isPlayMusic()) {
            play.setImageResource(R.drawable.play);
        } else if (!musicBinder.isPlayMusic()) {
            play.setImageResource(R.drawable.pause);
        }
    }

    public void next() {
        Intent nextIntent = new Intent();
        nextIntent.putExtra("Music", "Next");
        nextIntent.setAction("MusicChange");
        sendBroadcast(nextIntent);
        initLrc();
    }

    public void previous() {
        Intent previousIntent = new Intent();
        previousIntent.putExtra("Music", "Previous");
        previousIntent.setAction("MusicChange");
        sendBroadcast(previousIntent);
        initLrc();
    }

    //广播接收器
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("Music")) {
                case "change":
                    handler.sendEmptyMessage(MUSICDURATION);
                    handler.sendEmptyMessage(UPDATE);
                    title_str=intent.getStringExtra("musicTitle");
                    artist_str=intent.getStringExtra("musicArtist");
                    toolbar.setTitle(title_str);
                    toolbar.setSubtitle(artist_str);
                    musicUrl=intent.getStringExtra("musicUrl");
                    name=intent.getStringExtra("musicName");
                    musicSize=intent.getStringExtra("musicSize");
                    initLrc();
                    break;
                case "list":
                    SharedPreferences.Editor editor = context.getSharedPreferences(intent.getStringExtra("listName"), Context.MODE_PRIVATE).edit();
                    int j = 1;
                    while (context.getSharedPreferences(intent.getStringExtra("listName"), Context.MODE_PRIVATE).contains(Integer.toString(j)))
                        j++;
                    editor.putString(Integer.toString(j), musicUrl);
                    j++;
                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT);
                default:
                    break;
            }
        }
    }


}
