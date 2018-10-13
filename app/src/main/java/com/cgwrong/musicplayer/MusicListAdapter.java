package com.cgwrong.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by admin on 2018/4/15.
 */

public class MusicListAdapter extends ArrayAdapter<String>{

    private int resourceId;

    public MusicListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String str=getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.listName=(TextView) view.findViewById(R.id.list_name);
            viewHolder.musicNum=(TextView) view.findViewById(R.id.music_num);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.listName.setText(str);
        SharedPreferences pref=getContext().getSharedPreferences(str,Context.MODE_PRIVATE);
        int i=0;
        while(pref.contains(Integer.toString(i)))
            i++;
        viewHolder.musicNum.setText("共"+i+"首歌");
        return view;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    class ViewHolder{
        TextView listName;
        TextView musicNum;
    }
}
