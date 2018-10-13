package com.cgwrong.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private MediaPlayer player;

    private MusicBinder mBinder=new MusicBinder();

    private MediaPlayer[] mediaPlayers;

    class MusicBinder extends Binder{

        public void playMusic(String url){

            if(player.isPlaying()){
                player.stop();
                player.release();
                player=null;
            }
            player=MediaPlayer.create(MyApplication.getContext(),Uri.parse(url));
            player.setOnCompletionListener(new MyCompletionListener());
            player.start();

        }

        public void pauseMusic(){
            if(player.isPlaying()){
                player.pause();
            }
        }

        public void startMusic(){
            if(!player.isPlaying()){
                player.start();
            }
        }

        public boolean isPlayMusic(){
            return player.isPlaying();
        }

        public void repeatPlay(){
            player.seekTo(0);
        }

        public long getCurrent(){
            return player.getCurrentPosition();
        }

        public long getDuration(){
            return player.getDuration();
        }

        public void seekToMusic(int msec){
            player.seekTo(msec);
        }

        public void nextPlay(String url){

        }

    }

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the ncommunicatio channel to the service.
        return mBinder;

    }

    @Override
    public void onCreate(){
        super.onCreate();
        player=new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private class MyCompletionListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            Intent intent=new Intent();
            intent.putExtra("Music","finished");
            intent.setAction("MusicChange");
            sendBroadcast(intent);
        }
    }
}