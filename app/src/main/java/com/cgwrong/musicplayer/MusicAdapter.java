package com.cgwrong.musicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by admin on 2018/2/6.
 */

public class MusicAdapter extends ArrayAdapter<Music>{

    private int resourceId;

    private int position;

    public int mode=0;

    public MusicAdapter(Context context,int viewResourceId,List<Music> objects){
        super(context,viewResourceId,objects);
        resourceId=viewResourceId;
    }


    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        final Music music=getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.musicName=(TextView) view.findViewById(R.id.music_name);
            viewHolder.musicSinger=(TextView) view.findViewById(R.id.music_singer);
            viewHolder.next_play=(ImageButton) view.findViewById(R.id.add);
            viewHolder.check=(CheckBox) view.findViewById(R.id.check);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.musicName.setText(music.getMusicName());
        viewHolder.musicSinger.setText(music.getMusicSinger());

        //下一首播放
        final int p=position;
        viewHolder.next_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("MusicChange");
                intent.putExtra("Music","NextPlay");
                intent.putExtra("position",p);
                MyApplication.getContext().sendBroadcast(intent);
                Toast.makeText(MyApplication.getContext(),"成功添加到下一首播放",Toast.LENGTH_SHORT).show();
            }
        });

        if(position==this.position){
            view.setBackgroundColor(Color.parseColor("#666666"));
        }else{
            view.setBackgroundColor(Color.parseColor("#333333"));
        }

        //确定CheckBox的状态
        if(mode==1){
            viewHolder.check.setVisibility(View.VISIBLE);
        }else if(mode==2) {
            viewHolder.check.setChecked(true);
        }else if(mode==3) {
            viewHolder.check.setChecked(false);
        }else{
            viewHolder.check.setChecked(false);
            viewHolder.check.setVisibility(View.GONE);
        }

        viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    music.isSelected=1;
                }else if (!isChecked){
                    music.isSelected=0;
                }
            }
        });

        return view;
    }

    class ViewHolder{
        TextView musicName;
        TextView musicSinger;
        CheckBox check;
        ImageButton next_play;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    public void setPosition(int position){
        this.position=position;
    }


}
