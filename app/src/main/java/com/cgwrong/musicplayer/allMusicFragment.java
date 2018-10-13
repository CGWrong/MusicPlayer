package com.cgwrong.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2018/2/6.
 */

public class allMusicFragment extends Fragment implements View.OnClickListener{

    private List<Music> musicList=new ArrayList<>();

    private FragmentInteraction listtener;

    private MusicAdapter adapter;
    private ListView listView;

    private LinearLayout musicOperate;
    private View v1;
    private CheckBox allSelect;
    private ImageButton laterPlay;
    private ImageButton addTo;
    private ImageButton delete;
    private TextView selectText;

    private AddToListReceiver receiver;

    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        View view=inflater.inflate(R.layout.all_music_fragment,container,false);
        //音乐list设置
        initMusic();
        adapter=new MusicAdapter(MyApplication.getContext(),R.layout.music_layout,musicList);
        listView=(ListView) view.findViewById(R.id.music_list_view);
        listView.setAdapter(adapter);

        musicOperate=(LinearLayout) view.findViewById(R.id.music_operate);
        v1=(View) view.findViewById(R.id.view);
        allSelect=(CheckBox) view.findViewById(R.id.all_select);
        laterPlay=(ImageButton) view.findViewById(R.id.later_play);
        addTo=(ImageButton) view.findViewById(R.id.add_to);
        delete=(ImageButton) view.findViewById(R.id.music_delete);
        selectText=(TextView) view.findViewById(R.id.select_text);

        laterPlay.setOnClickListener(this);
        addTo.setOnClickListener(this);
        delete.setOnClickListener(this);

        //viewlist点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                if(adapter.mode==1) {
                    CheckBox checkBox=(CheckBox)view.findViewById(R.id.check);
                    if(checkBox.isChecked()){
                        checkBox.setChecked(false);
                        musicList.get(position).isSelected=0;
                    }else {
                        checkBox.setChecked(true);
                        musicList.get(position).isSelected=1;
                    }
                }else{
                    Music music = musicList.get(position);
                    listtener.process(music.getMusicName(), music.getMusicSinger(), position, music.getMusicUrl(), music.getSize(), music.getAllName());
                    adapter.setPosition(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //viewlist长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                musicOperate.setVisibility(View.VISIBLE);
                selectText.setText("你选择了0项");
                v1.setVisibility(View.VISIBLE);
                adapter.mode=1;
                adapter.notifyDataSetChanged();
                Intent intent=new Intent("MusicChange");
                intent.putExtra("Music","Back");
                intent.putExtra("Mode",1);
                MyApplication.getContext().sendBroadcast(intent);
                return true;
            }
        });

        //全选点击事件
        allSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    adapter.mode=2;
                    adapter.notifyDataSetChanged();
                }else if(isChecked==false){
                    adapter.mode=3;
                    adapter.notifyDataSetChanged();
                }
            }
        });

        receiver=new AddToListReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("MusicChange");
        getContext().registerReceiver(receiver,filter);

        return view;
    }

    //初始化音乐list
    private void initMusic(){
            Music music;
            Cursor cursor = MyApplication.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    music = new Music(title, singer, name, uri, size);
                    musicList.add(music);
                } while (cursor.moveToNext());
            }
            cursor.close();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(activity instanceof FragmentInteraction)
            listtener=(FragmentInteraction)activity;
        else
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listtener=null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.later_play:
                for (int i=0;i<musicList.size();i++){
                    if (musicList.get(i).isSelected==1){
                        Intent intent=new Intent("MusicChange");
                        intent.putExtra("Music","NextPlay");
                        intent.putExtra("position",i);
                        MyApplication.getContext().sendBroadcast(intent);
                        Toast.makeText(MyApplication.getContext(),"成功添加到下一首播放",Toast.LENGTH_SHORT).show();
                    }
                }
                musicOperate.setVisibility(View.GONE);
                v1.setVisibility(View.GONE);
                Intent intent=new Intent("MusicChange");
                intent.putExtra("Music","Back");
                intent.putExtra("Mode",0);
                MyApplication.getContext().sendBroadcast(intent);
                adapter.mode=0;
                adapter.notifyDataSetChanged();
                break;
            case R.id.music_delete:
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setTitle("提示");
                dialog.setMessage("是否删除所选歌曲？");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i=0;i<musicList.size();i++){
                            if(musicList.get(i).isSelected==1){
                                String uri=musicList.get(i).getMusicUri();
                                File f=new File(uri);
                                if(f.exists()) {
                                    f.delete();
                                    musicList.remove(i);
                                    String where=MediaStore.Video.Media.DATA+"='"+uri+"'";
                                    getContext().getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,where,null);
                                }
                            }
                        }
                        musicOperate.setVisibility(View.GONE);
                        v1.setVisibility(View.GONE);
                        Intent intent=new Intent("MusicChange");
                        intent.putExtra("Music","Back");
                        intent.putExtra("Mode",0);
                        MyApplication.getContext().sendBroadcast(intent);
                        adapter.mode=0;
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                break;
            case R.id.add_to:
                AddToListDialog.Builder addDialog=new AddToListDialog.Builder(getContext());
                addDialog.setListView();
                addDialog.create()
                        .show();
                break;
            default:
                break;
        }
    }

    public interface FragmentInteraction{
        void process(String string1,String string2,int position,String url,String size,String name);
    }

    public void randomPlay(){
        Random random=new Random();
        int pst=random.nextInt(adapter.getCount()+1);
        Music music=musicList.get(pst);
        listtener.process(music.getMusicName(),music.getMusicSinger(),pst,music.getMusicUrl(),music.getSize(),music.getAllName());
        adapter.setPosition(pst);
        adapter.notifyDataSetChanged();
        position=pst;
    }

    public void listrepeatPlaynext(int i){
        int pst=i+1;
        if(pst>adapter.getCount()) {
            pst = 1;
        }
        Music music=musicList.get(pst);
        listtener.process(music.getMusicName(),music.getMusicSinger(),pst,music.getMusicUrl(),music.getSize(),music.getAllName());
        adapter.setPosition(pst);
        adapter.notifyDataSetChanged();
        position=pst;
    }

    public void listrepeatPlayprevious(int i){
        int pst=i-1;
        if(pst<1){
            pst=adapter.getCount();
        }
        Music music=musicList.get(pst);
        listtener.process(music.getMusicName(),music.getMusicSinger(),pst,music.getMusicUrl(),music.getSize(),music.getAllName());
        adapter.setPosition(pst);
        adapter.notifyDataSetChanged();
        position=pst;
    }

    public void nextPlay(int i){
        Music music=musicList.get(i);
        listtener.process(music.getMusicName(),music.getMusicSinger(),i,music.getMusicUrl(),music.getSize(),music.getAllName());
        adapter.setPosition(i);
        adapter.notifyDataSetChanged();
        position=i;
    }

    public int getPosition(){
        return position;
    }

    public void updateMusic(String s){
        List<Music> searchMusic=new ArrayList<>();
        Cursor cursor=MyApplication.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        int i=cursor.getCount();
        if(cursor.moveToFirst()){
            do{
                String title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String singer=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String uri=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String size=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                if(name!=null&&singer!=null&&title!=null) {
                    if (name.toLowerCase().contains(s) || singer.toLowerCase().contains(s) || title.toLowerCase().contains(s)) {
                        Music music = new Music(title, singer, name, uri, size);
                        searchMusic.add(music);
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        musicList.clear();
        musicList.addAll(searchMusic);
        adapter.notifyDataSetChanged();
    }

    public void callBack(){
        musicOperate.setVisibility(View.GONE);
        v1.setVisibility(View.GONE);
        adapter.mode=0;
        adapter.notifyDataSetChanged();
    }

    public class AddToListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("Music")=="list") {
                SharedPreferences.Editor editor = context.getSharedPreferences(intent.getStringExtra("listName"), Context.MODE_PRIVATE).edit();
                int j = 1;
                while (context.getSharedPreferences(intent.getStringExtra("listName"), Context.MODE_PRIVATE).contains(Integer.toString(j)))
                    j++;
                for (int i = 0; i < musicList.size(); i++) {
                    if (musicList.get(i).isSelected == 1) {
                        Music music = musicList.get(i);
                        editor.putString(Integer.toString(j), music.getMusicUrl());
                        j++;
                    }
                }
                callBack();
                Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT);
            }
        }
    }

    public void listViewToTop(){
        listView.setSelectionAfterHeaderView();
    }

    public void listViewSelection(int i){
        listView.setSelection(i);
    }
}
